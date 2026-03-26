package com.tik.aipushpushservice.service.impl;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.tik.aipushpushservice.bean.SkillResult;
import com.tik.aipushpushservice.service.AISummaryService;
import com.tik.aipushpushservice.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AISummaryServiceImpl implements AISummaryService {

    private final ChatClient qwenChatClient;
    private final EmbeddingModel embeddingModel;

    @Value("${spring.ai.dashscope.chat.options.model:qwen-plus}")
    private String chatModel;

    @Value("${spring.ai.dashscope.chat.options.temperature:0.7}")
    private Double temperature;

    @Value("${spring.ai.dashscope.chat.options.max-tokens:1000}")
    private Integer maxTokens;

    @Value("${spring.ai.dashscope.embedding.options.model:text-embedding-v4}")
    private String embeddingModelName;

    @Value("${vector.dimensions:1536}")
    private Integer embeddingDimensions;

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
            String prompt = template
                    .replace("{data}", JsonUtils.toJson(data))
                    .replace("{references}", formatReferences(skillResult));

            String content = qwenChatClient.prompt()
                    .system("你是一个智能助手，请根据用户的问题提供准确、简洁、可执行的回答。")
                    .user(prompt)
                    .options(DashScopeChatOptions.builder()
                            .withModel(chatModel)
                            .withTemperature(temperature)
                            .withMaxToken(maxTokens)
                            .build())
                    .call()
                    .content();

            return content == null ? "AI总结失败" : content;
        } catch (Exception e) {
            log.error("AI总结失败", e);
            return "AI总结失败: " + e.getMessage();
        }
    }

    @Override
    public float[] generateEmbedding(String text) {
        try {
            EmbeddingResponse response = embeddingModel.call(new EmbeddingRequest(
                    List.of(text),
                    DashScopeEmbeddingOptions.builder()
                            .withModel(embeddingModelName)
                            .withDimensions(embeddingDimensions)
                            .build()
            ));
            if (response != null && response.getResult() != null) {
                return response.getResult().getOutput();
            }
        } catch (Exception e) {
            log.error("生成向量失败", e);
        }
        return new float[embeddingDimensions];
    }

    @Override
    public Map<String, Object> extractParams(String text, String type) {
        try {
            String content = qwenChatClient.prompt()
                    .system("""
                            你是一个参数提取助手。
                            你只能输出 JSON 对象。
                            不要输出 markdown，不要输出代码块，不要输出解释。
                            """)
                    .user("""
                            请从下面的用户消息中提取参数，并返回 JSON。

                            参数类型：%s
                            用户消息：%s
                            """.formatted(type, text))
                    .options(DashScopeChatOptions.builder()
                            .withModel(chatModel)
                            .withTemperature(0.1)
                            .build())
                    .call()
                    .content();

            Map<String, Object> params = JsonUtils.toMap(content);
            return params == null ? new HashMap<>() : params;
        } catch (Exception e) {
            log.error("提取参数失败", e);
            return new HashMap<>();
        }
    }

    private String formatReferences(SkillResult skillResult) {
        if (skillResult == null || skillResult.getReferences() == null || skillResult.getReferences().isEmpty()) {
            return "无参考信息";
        }

        return skillResult.getReferences().stream()
                .map(ref -> String.format("- 【%s】(来自:%s, 相关度:%.2f)\n  %s",
                        ref.getTitle(), ref.getSource(), ref.getScore(), ref.getContent()))
                .collect(Collectors.joining("\n"));
    }
}
