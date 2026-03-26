package com.tik.aipushpushservice.service;

import com.tik.aipushpushservice.bean.SkillResult;

import java.util.List;
import java.util.Map;

public interface VectorSearchService {

    /**
     * 向量搜索
     */
    List<Map<String, Object>> search(String query, int topK);

    /**
     * 混合搜索
     */
//    List<Map<String, Object>> hybridSearch(String query, double minScore, int topK);

    /**
     * 添加资源
     */
    void addResource(String id, String type, String name, String description,
                     List<String> keywords, Map<String, Object> metadata);

    /**
     * 批量添加资源
     */
    void batchAddResources(List<Map<String, Object>> resources);

    /**
     * 更新权重
     */
    void updateWeight(String id, double weight);

    /**
     * 获取技能结果
     */
    SkillResult getSkillResult(String query, String skillType);
}