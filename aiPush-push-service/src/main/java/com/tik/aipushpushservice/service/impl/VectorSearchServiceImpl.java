package com.tik.aipushpushservice.service.impl;

import com.tik.aipushpushservice.bean.SkillResult;
import com.tik.aipushpushservice.service.VectorSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorSearchServiceImpl implements VectorSearchService {

    private final VectorStore vectorStore;

    @Value("${vector.top-k:5}")
    private int defaultTopK;

    @Value("${vector.min-score:0.6}")
    private double defaultMinScore;

    @Override
    public List<Map<String, Object>> search(String query, int topK) {
        return doSearch(query, defaultMinScore, topK);
    }

    @Override
    public List<Map<String, Object>> hybridSearch(String query, double minScore, int topK) {
        return doSearch(query, minScore, topK);
    }

    @Override
    public SkillResult getSkillResult(String query, String skillType) {
        try {
            log.info("Search skill result, query: {}, skillType: {}", query, skillType);

            List<SkillResult.Reference> references = hybridSearch(query, defaultMinScore, defaultTopK)
                    .stream()
                    .map(result -> SkillResult.Reference.builder()
                            .title(String.valueOf(result.getOrDefault("name", "unknown")))
                            .content(String.valueOf(result.getOrDefault("description", result.get("content"))))
                            .source(String.valueOf(result.getOrDefault("resourceType", "vector_store")))
                            .score(toScore(result.get("similarity")))
                            .metadata(result)
                            .build())
                    .collect(Collectors.toList());

            double confidence = references.stream()
                    .mapToDouble(SkillResult.Reference::getScore)
                    .max()
                    .orElse(0D);

            return SkillResult.builder()
                    .skillName(skillType)
                    .references(references)
                    .confidence(confidence)
                    .metadata(Map.of(
                            "totalResults", references.size(),
                            "query", query
                    ))
                    .build();
        } catch (Exception e) {
            log.error("Search skill result failed", e);
            return SkillResult.builder()
                    .skillName(skillType)
                    .references(List.of())
                    .confidence(0D)
                    .metadata(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @Override
    public void addResource(String id, String type, String name, String description,
                            List<String> keywords, Map<String, Object> metadata) {
        try {
            String content = String.format("类型：%s，名称：%s，描述：%s，关键词：%s",
                    type, name, description, String.join(",", keywords));

            Map<String, Object> docMetadata = new HashMap<>();
            if (metadata != null) {
                docMetadata.putAll(metadata);
            }
            docMetadata.put("id", id);
            docMetadata.put("name", name);
            docMetadata.put("description", description);
            docMetadata.put("resourceType", type);
            docMetadata.put("resource_type", type);
            docMetadata.put("keywords", keywords);

            vectorStore.add(List.of(new Document(id, content, docMetadata)));
            log.info("Added vector resource: {}", id);
        } catch (Exception e) {
            log.error("Add vector resource failed", e);
            throw new RuntimeException("Add vector resource failed", e);
        }
    }

    @Override
    public void batchAddResources(List<Map<String, Object>> resources) {
        try {
            List<Document> documents = new ArrayList<>();
            for (Map<String, Object> resource : resources) {
                String type = String.valueOf(resource.getOrDefault("resourceType",
                        resource.getOrDefault("resource_type", "vector_store")));
                String name = String.valueOf(resource.getOrDefault("name", "unknown"));
                String description = String.valueOf(resource.getOrDefault("description", ""));
                List<String> keywords = resource.get("keywords") instanceof List<?> list
                        ? list.stream().map(String::valueOf).toList()
                        : List.of();

                String content = String.format("类型：%s，名称：%s，描述：%s，关键词：%s",
                        type, name, description, String.join(",", keywords));
                String id = String.valueOf(resource.getOrDefault("id", UUID.randomUUID().toString()));

                Map<String, Object> docMetadata = new HashMap<>(resource);
                docMetadata.put("name", name);
                docMetadata.put("description", description);
                docMetadata.put("resourceType", type);
                docMetadata.put("resource_type", type);
                docMetadata.put("keywords", keywords);

                documents.add(new Document(id, content, docMetadata));
            }

            vectorStore.add(documents);
            log.info("Batch added vector resources: {}", documents.size());
        } catch (Exception e) {
            log.error("Batch add vector resources failed", e);
            throw new RuntimeException("Batch add vector resources failed", e);
        }
    }

    @Override
    public void updateWeight(String id, double weight) {
        log.warn("VectorStore does not support direct weight update. id: {}, weight: {}", id, weight);
    }

    private List<Map<String, Object>> doSearch(String query, double minScore, int topK) {
        try {
            log.info("Vector search, query: {}, minScore: {}, topK: {}", query, minScore, topK);

            List<Document> documents = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(query)
                            .topK(topK > 0 ? topK : defaultTopK)
                            .similarityThreshold(minScore > 0 ? minScore : defaultMinScore)
                            .build()
            );

            return documents.stream()
                    .map(this::convertToMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Vector search failed", e);
            return new ArrayList<>();
        }
    }

    private Map<String, Object> convertToMap(Document doc) {
        Map<String, Object> result = new HashMap<>(doc.getMetadata());
        Object resourceType = result.getOrDefault("resourceType",
                result.getOrDefault("resource_type", "vector_store"));
        String content = doc.getText();
        Object description = result.getOrDefault("description", content);

        result.put("id", doc.getId());
        result.put("content", content);
        result.put("description", description);
        result.put("resourceType", resourceType);
        result.put("resource_type", resourceType);
        result.put("similarity", toScore(result.get("distance")));
        return result;
    }

    private double toScore(Object value) {
        if (!(value instanceof Number number)) {
            return 0D;
        }

        double raw = number.doubleValue();
        if (raw < 0D) {
            return 0D;
        }
        if (raw <= 1D) {
            return raw;
        }
        return 1D / (1D + raw);
    }
}
