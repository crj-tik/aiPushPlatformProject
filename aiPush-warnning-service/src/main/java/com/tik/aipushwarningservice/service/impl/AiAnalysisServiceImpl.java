package com.tik.aipushwarningservice.service.impl;

import com.tik.aipushwarningservice.bean.MonitorRecord;
import com.tik.aipushwarningservice.bean.RiskAnalysisDTO;
import com.tik.aipushwarningservice.mapper.MonitorMapper;
import com.tik.aipushwarningservice.service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalysisServiceImpl implements AiAnalysisService {

    @Autowired
    private final ChatClient accurateChatClient;
    private final MonitorMapper monitorMapper;

    @Override
    public RiskAnalysisDTO analyzePersonRisk(Long personId, String personName,
                                             String department, String position,
                                             String workData) {
        log.info("开始分析人员风险: personId={}, personName={}", personId, personName);

        // 构建提示词模板
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

        Map<String, Object> params = Map.of(
                "name", personName,
                "department", department,
                "position", position,
                "workData", workData
        );

        // 调用AI
        String aiResponse = accurateChatClient.prompt()
                .user(promptTemplate.create(params).getContents())
                .call()
                .content();

        log.info("AI分析完成，原始响应: {}", aiResponse);

        // 解析AI响应
        RiskAnalysisDTO analysisDTO = parseAiResponse(aiResponse);
        analysisDTO.setPersonId(personId);
        analysisDTO.setPersonName(personName);
        analysisDTO.setAnalysisType("人员风险评估");

        // 保存到数据库
        saveAnalysisResult(analysisDTO);

        return analysisDTO;
    }

    @Override
    public Flux<String> analyzePersonRiskStream(Long personId) {
        log.info("开始流式分析人员风险: personId={}", personId);

        // 构建提示词
        String prompt = String.format("请分析人员ID为 %d 的风险状况，返回详细的评估结果", personId);

        // 调用AI流式接口
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
        record.setAnalysisType(analysisDTO.getAnalysisType());
        record.setAnalysisResult(analysisDTO.getAnalysisResult());
        record.setRiskLevel(analysisDTO.getRiskLevel());
        record.setRiskScore(analysisDTO.getRiskScore());
        record.setSuggestions(analysisDTO.getSuggestions());
        record.setAiModel("qwen-max");
        record.setCreateTime(LocalDateTime.now());
        record.setCreateBy("SYSTEM");
        record.setDeleted(0);

        monitorMapper.insert(record);
        log.info("风险分析结果已保存到数据库, id: {}", record.getId());
    }

    @Override
    public List<MonitorRecord> getPersonAnalysisHistory(Long personId) {
        return monitorMapper.selectRecentByPersonId(personId, 10);
    }

    /**
     * 解析AI响应
     */
    private RiskAnalysisDTO parseAiResponse(String response) {
        RiskAnalysisDTO dto = new RiskAnalysisDTO();
        dto.setAnalysisResult(response);

        // 提取风险等级
        Pattern riskLevelPattern = Pattern.compile("风险等级[：:]\\s*(.+)");
        Matcher riskLevelMatcher = riskLevelPattern.matcher(response);
        if (riskLevelMatcher.find()) {
            String level = riskLevelMatcher.group(1).trim();
            dto.setRiskLevel(mapRiskLevel(level));
        } else {
            dto.setRiskLevel("MEDIUM"); // 默认值
        }

        // 提取风险分数
        Pattern scorePattern = Pattern.compile("风险分数[：:]\\s*(\\d+)");
        Matcher scoreMatcher = scorePattern.matcher(response);
        if (scoreMatcher.find()) {
            try {
                dto.setRiskScore(Integer.parseInt(scoreMatcher.group(1)));
            } catch (NumberFormatException e) {
                dto.setRiskScore(50); // 默认值
            }
        } else {
            dto.setRiskScore(50);
        }

        // 提取改进建议
        Pattern suggestionPattern = Pattern.compile("改进建议[：:]([\\s\\S]+?)(?=\\n\\s*\\n|$)", Pattern.DOTALL);
        Matcher suggestionMatcher = suggestionPattern.matcher(response);
        if (suggestionMatcher.find()) {
            dto.setSuggestions(suggestionMatcher.group(1).trim());
        }

        return dto;
    }

    /**
     * 映射风险等级
     */
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