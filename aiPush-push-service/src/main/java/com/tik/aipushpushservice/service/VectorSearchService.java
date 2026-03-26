package com.tik.aipushpushservice.service;

import com.tik.aipushpushservice.bean.SkillResult;

import java.util.List;
import java.util.Map;

public interface VectorSearchService {

    List<Map<String, Object>> search(String query, int topK);

    List<Map<String, Object>> hybridSearch(String query, double minScore, int topK);

    void addResource(String id, String type, String name, String description,
                     List<String> keywords, Map<String, Object> metadata);

    void batchAddResources(List<Map<String, Object>> resources);

    void updateWeight(String id, double weight);

    SkillResult getSkillResult(String query, String skillType);
}
