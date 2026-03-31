package com.tik.aipushwarningservice.service.impl;

import com.tik.aipushcommon.message.AlertPushRequest;
import com.tik.aipushwarningservice.bean.MonitorRecord;
import com.tik.aipushwarningservice.bean.RiskAnalysisDTO;
import com.tik.aipushwarningservice.mapper.MonitorMapper;
import com.tik.aipushwarningservice.service.AiAnalysisService;
import com.tik.aipushwarningservice.service.AlertPushProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalysisServiceImpl implements AiAnalysisService {

    @Autowired
    private final ChatClient accurateChatClient;
    private final MonitorMapper monitorMapper;
    private final AlertPushProducer alertPushProducer;

    @Value("${warning.ai.accurate.model:qwen-max}")
    private String accurateModel;

    @Override
    public RiskAnalysisDTO analyzePersonRisk(Long personId, String personName,
                                             String personIdCard,
                                             String department, String position,
                                             String workData) {
        log.info("开始分析人员风险: personId={}, personName={}", personId, personName);

        PromptTemplate promptTemplate = new PromptTemplate("""
                你是一个专业的人力资源风险分析师。请分析以下人员信息：

                人员信息：
                - 姓名：{name}
                - 部门：{department}
                - 岗位：{position}
                - 工作数据：{workData}

                请按照以下格式返回分析结果：
                风险等级：[高风险/中风险/低风险]
                风险分数：[0-100的整数]
                风险分析：[详细的风险分析，包括工作状态、潜在风险等]
                改进建议：[具体的改进建议，至少3条]
                """);

        Map<String, Object> params = new HashMap<>();
        params.put("name", personName);
        params.put("department", department);
        params.put("position", position);
        params.put("workData", workData);

        String aiResponse = accurateChatClient.prompt()
                .user(promptTemplate.create(params).getContents())
                .call()
                .content();

        log.info("AI分析完成，原始响应: {}", aiResponse);

        RiskAnalysisDTO analysisDTO = parseAiResponse(aiResponse);
        analysisDTO.setPersonId(personId);
        analysisDTO.setPersonName(personName);
        analysisDTO.setPersonIdCard(personIdCard);
        analysisDTO.setDepartment(department);
        analysisDTO.setPosition(position);
        analysisDTO.setAnalysisType("人员风险评估");
        analysisDTO.setAnalysisContent(workData);

        saveAnalysisResult(analysisDTO);
        sendAlertPushMessage(analysisDTO);
        return analysisDTO;
    }

    @Override
    public Flux<String> analyzePersonRiskStream(Long personId) {
        log.info("开始流式分析人员风险: personId={}", personId);

        String prompt = String.format("请分析人员ID为 %d 的风险状况，返回详细的评估结果", personId);

        return accurateChatClient.prompt()
                .user(prompt)
                .stream()
                .content()
                .doOnComplete(() -> log.info("流式分析完成: personId={}", personId))
                .doOnError(error -> log.error("流式分析失败: personId={}, error={}", personId, error.getMessage()));
    }

    @Override
    public void saveAnalysisResult(RiskAnalysisDTO analysisDTO) {
        MonitorRecord record = new MonitorRecord();
        record.setPersonId(analysisDTO.getPersonId());
        record.setPersonName(analysisDTO.getPersonName());
        record.setPersonIdCard(analysisDTO.getPersonIdCard());
        record.setDepartment(analysisDTO.getDepartment());
        record.setPosition(analysisDTO.getPosition());
        record.setAnalysisType(analysisDTO.getAnalysisType());
        record.setAnalysisContent(analysisDTO.getAnalysisContent());
        record.setAnalysisResult(analysisDTO.getAnalysisResult());
        record.setRiskLevel(analysisDTO.getRiskLevel());
        record.setRiskScore(analysisDTO.getRiskScore());
        record.setSuggestions(analysisDTO.getSuggestions());
        record.setAiModel(accurateModel);
        record.setCreateTime(LocalDateTime.now());
        record.setCreateBy("SYSTEM");
        record.setDeleted(0);

        monitorMapper.insert(record);
        log.info("风险分析结果已保存到数据库, id={}", record.getId());
    }

    @Override
    public List<MonitorRecord> getPersonAnalysisHistory(Long personId) {
        return monitorMapper.selectRecentByPersonId(personId, 10);
    }

    private void sendAlertPushMessage(RiskAnalysisDTO analysisDTO) {
        AlertPushRequest request = new AlertPushRequest();
        request.setTraceId(UUID.randomUUID().toString());
        request.setPersonId(analysisDTO.getPersonId());
        request.setPersonName(analysisDTO.getPersonName());
        request.setIdcard(analysisDTO.getPersonIdCard());
        request.setDepartment(analysisDTO.getDepartment());
        request.setPosition(analysisDTO.getPosition());
        request.setAlertType(analysisDTO.getAnalysisType());
        request.setAlertLevel(analysisDTO.getRiskLevel());
        request.setAlertScore(analysisDTO.getRiskScore());
        request.setAlertTitle("人员风险告警");
        request.setAlertContent(analysisDTO.getAnalysisContent());
        request.setAnalysisResult(analysisDTO.getAnalysisResult());
        request.setSuggestions(analysisDTO.getSuggestions());
        request.setAlertTime(LocalDateTime.now());

        Map<String, Object> ext = new HashMap<>();
        ext.put("source", "warning-service");
        ext.put("personId", analysisDTO.getPersonId());
        request.setExt(ext);

        alertPushProducer.send(request);
    }

    private RiskAnalysisDTO parseAiResponse(String response) {
        RiskAnalysisDTO dto = new RiskAnalysisDTO();
        dto.setAnalysisResult(response);

        Pattern riskLevelPattern = Pattern.compile("风险等级[：:]\\s*(.+)");
        Matcher riskLevelMatcher = riskLevelPattern.matcher(response);
        if (riskLevelMatcher.find()) {
            dto.setRiskLevel(mapRiskLevel(riskLevelMatcher.group(1).trim()));
        } else {
            dto.setRiskLevel("MEDIUM");
        }

        Pattern scorePattern = Pattern.compile("风险分数[：:]\\s*(\\d+)");
        Matcher scoreMatcher = scorePattern.matcher(response);
        if (scoreMatcher.find()) {
            try {
                dto.setRiskScore(Integer.parseInt(scoreMatcher.group(1)));
            } catch (NumberFormatException e) {
                dto.setRiskScore(50);
            }
        } else {
            dto.setRiskScore(50);
        }

        Pattern suggestionPattern = Pattern.compile("改进建议[：:]([\\s\\S]+?)(?=\\n\\s*\\n|$)", Pattern.DOTALL);
        Matcher suggestionMatcher = suggestionPattern.matcher(response);
        if (suggestionMatcher.find()) {
            dto.setSuggestions(suggestionMatcher.group(1).trim());
        }

        return dto;
    }

    private String mapRiskLevel(String level) {
        if (level.contains("高")) {
            return "HIGH";
        } else if (level.contains("中")) {
            return "MEDIUM";
        } else if (level.contains("低")) {
            return "LOW";
        }
        return "MEDIUM";
    }
}
