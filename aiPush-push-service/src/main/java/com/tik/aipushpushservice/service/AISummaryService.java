package com.tik.aipushpushservice.service;

import com.tik.aipushpushservice.bean.SkillResult;

import java.util.Map;

public interface AISummaryService {

    /**
     * AI总结
     */
    String summarize(Map<String, Object> data, String summaryType, SkillResult skillResult);

    /**
     * 生成向量
     */
    float[] generateEmbedding(String text);

    /**
     * 提取参数
     */
    Map<String, Object> extractParams(String text, String type);
}