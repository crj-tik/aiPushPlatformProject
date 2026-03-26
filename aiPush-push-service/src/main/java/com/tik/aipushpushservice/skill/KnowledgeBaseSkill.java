package com.tik.aipushpushservice.skill;

import com.tik.aipushpushservice.bean.SkillResult;
import com.tik.aipushpushservice.service.VectorSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeBaseSkill implements Skill {

    private static final double DEFAULT_MIN_SCORE = 0.6D;
    private static final int DEFAULT_TOP_K = 5;

    private final VectorSearchService vectorSearchService;

    @Override
    public boolean match(String message, Map<String, Object> context) {
        return true;
    }

    @Override
    public SkillResult execute(String message, Map<String, Object> context) {
        try {
            List<Map<String, Object>> results = vectorSearchService.hybridSearch(message, DEFAULT_MIN_SCORE, DEFAULT_TOP_K);

            List<SkillResult.Reference> references = results.stream()
                    .map(result -> SkillResult.Reference.builder()
                            .title(String.valueOf(result.getOrDefault("name", "unknown")))
                            .content(String.valueOf(result.getOrDefault("description", result.get("content"))))
                            .source(String.valueOf(result.getOrDefault("resourceType", "vector_store")))
                            .score(toDouble(result.get("similarity")))
                            .metadata(result)
                            .build())
                    .collect(Collectors.toList());

            double confidence = references.stream()
                    .mapToDouble(SkillResult.Reference::getScore)
                    .max()
                    .orElse(0D);

            return SkillResult.builder()
                    .skillName(getName())
                    .references(references)
                    .confidence(confidence)
                    .metadata(Map.of("total", results.size()))
                    .build();
        } catch (Exception e) {
            log.error("Execute knowledge base skill failed", e);
            return SkillResult.builder()
                    .skillName(getName())
                    .references(List.of())
                    .confidence(0D)
                    .metadata(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @Override
    public String getName() {
        return "knowledge_base";
    }

    @Override
    public String getDescription() {
        return "Knowledge base retrieval skill";
    }

    @Override
    public int getPriority() {
        return 10;
    }

    private double toDouble(Object value) {
        return value instanceof Number number ? number.doubleValue() : 0D;
    }
}
