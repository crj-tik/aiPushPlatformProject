package com.tik.aipushpushservice.service.impl;

import com.tik.aipushpushservice.bean.SkillResult;
import com.tik.aipushpushservice.service.AISummaryService;
import com.tik.aipushpushservice.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AISummaryServiceImpl implements AISummaryService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.openai.api-key}")
    private String apiKey;

    @Value("${ai.openai.base-url}")
    private String baseUrl;

    @Value("${ai.openai.chat.model}")
    private String model;

    @Value("${ai.openai.chat.temperature}")
    private Double temperature;

    @Value("${ai.openai.chat.max-tokens}")
    private Integer maxTokens;

    private final Map<String, String> summaryTemplates = Map.of(
            "daily_report", "你是一个数据分析助手。请将以下业务数据整理成一份简洁的日报，包含关键指标、趋势分析和改进建议：\n{data}\n\n参考信息：\n{references}",
            "alert", "你是一个运维专家。请对以下告警信息进行总结，说明问题原因、影响范围和紧急程度：\n{data}\n\n参考信息：\n{references}",
            "statistics", "你是一个数据可视化专家。请从以下统计数据中提炼出3-5个核心洞察，用通俗的语言表达：\n{data}\n\n参考信息：\n{references}",
            "default", "请对以下数据进行简要总结，提取最重要的信息：\n{data}\n\n参考信息：\n{references}"
    );

    @Override
    public String summarize(Map<String, Object> data, String summaryType, SkillResult skillResult) {
        try {
            String template = summaryTemplates.getOrDefault(summaryType, summaryTemplates.get("default"));

            // 格式化数据
            String dataStr = JsonUtils.toJson(data);

            // 格式化参考信息
            String referencesStr = formatReferences(skillResult);

            String prompt = template
                    .replace("{data}", dataStr)
                    .replace("{references}", referencesStr);

            // 调用OpenAI API
            Map<String, Object> request = buildChatRequest(prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            Map<String, Object> response = restTemplate.postForObject(
                    baseUrl + "/chat/completions",
                    entity,
                    Map.class
            );

            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }

            return "AI总结失败";

        } catch (Exception e) {
            log.error("AI总结失败", e);
            return "AI总结失败: " + e.getMessage();
        }
    }

    @Override
    public float[] generateEmbedding(String text) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("input", text);
            request.put("model", "text-embedding-ada-002");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            Map<String, Object> response = restTemplate.postForObject(
                    baseUrl + "/embeddings",
                    entity,
                    Map.class
            );

            if (response != null && response.containsKey("data")) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
                if (!data.isEmpty()) {
                    List<Double> embeddingList = (List<Double>) data.get(0).get("embedding");
                    float[] embedding = new float[embeddingList.size()];
                    for (int i = 0; i < embeddingList.size(); i++) {
                        embedding[i] = embeddingList.get(i).floatValue();
                    }
                    return embedding;
                }
            }

        } catch (Exception e) {
            log.error("生成向量失败", e);
        }

        return new float[1536];
    }

    @Override
    public Map<String, Object> extractParams(String text, String type) {
        String prompt = String.format("""
            从用户消息中提取参数，返回JSON格式。
            
            用户消息：%s
            参数类型：%s
            
            请只返回JSON，不要有其他说明。
            """, text, type);

        Map<String, Object> request = buildChatRequest(prompt);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            Map<String, Object> response = restTemplate.postForObject(
                    baseUrl + "/chat/completions",
                    entity,
                    Map.class
            );

            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    return JsonUtils.toMap(content);
                }
            }

        } catch (Exception e) {
            log.error("提取参数失败", e);
        }

        return new HashMap<>();
    }

    /**
     * 构建聊天请求
     */
    private Map<String, Object> buildChatRequest(String prompt) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", model);
        request.put("temperature", temperature);
        request.put("max_tokens", maxTokens);

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "你是一个智能助手，请根据用户的问题提供准确的回答。"),
                Map.of("role", "user", "content", prompt)
        );

        request.put("messages", messages);

        return request;
    }

    /**
     * 格式化参考信息
     */
    private String formatReferences(SkillResult skillResult) {
        if (skillResult == null || skillResult.getReferences() == null) {
            return "无参考信息";
        }

        return skillResult.getReferences().stream()
                .map(ref -> String.format("- 【%s】(来自:%s, 相关度:%.2f)\n  %s",
                        ref.getTitle(), ref.getSource(), ref.getScore(), ref.getContent()))
                .collect(Collectors.joining("\n"));
    }
}
