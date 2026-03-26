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

    private final VectorSearchService vectorSearchService;

    @Override
    public boolean match(String message, Map<String, Object> context) {
        return true;  // 作为默认技能
    }

    @Override
    public SkillResult execute(String message, Map<String, Object> context) {
        try {
            // 执行向量搜索
            List<Map<String, Object>> results = vectorSearchService.hybridSearch(message, 0.6, 5);

            // 构建参考信息
            List<SkillResult.Reference> references = results.stream()
                    .map(result -> SkillResult.Reference.builder()
                            .title((String) result.get("name"))
                            .content((String) result.get("description"))
                            .source((String) result.get("resourceType"))
                            .score(((Number) result.get("similarity")).doubleValue())
                            .build())
                    .collect(Collectors.toList());

            double confidence = results.isEmpty() ? 0 :
                    ((Number) results.get(0).get("similarity")).doubleValue();

            return SkillResult.builder()
                    .skillName(getName())
                    .references(references)
                    .confidence(confidence)
                    .metadata(Map.of("total", results.size()))
                    .build();

        } catch (Exception e) {
            log.error("知识库技能执行失败", e);
            return SkillResult.builder()
                    .skillName(getName())
                    .references(List.of())
                    .confidence(Double.valueOf(0))
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
        return "知识库问答技能";
    }

    @Override
    public int getPriority() {
        return 10;
    }
}
