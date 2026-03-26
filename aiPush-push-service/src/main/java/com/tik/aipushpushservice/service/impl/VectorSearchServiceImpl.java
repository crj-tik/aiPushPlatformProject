package com.tik.aipushpushservice.service.impl;

import com.tik.aipushpushservice.bean.SkillResult;
import com.tik.aipushpushservice.service.VectorSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorSearchServiceImpl implements VectorSearchService {

    // 只用 VectorStore，不再需要 Mapper 和 EmbeddingModel
    private final VectorStore vectorStore;

    @Value("${vector-search.top-k:5}")
    private int defaultTopK;

    @Value("${vector-search.min-score:0.6}")
    private double defaultMinScore;

    @Override
    public List<Map<String, Object>> search(String query, int topK) {
        try {
            log.info("执行向量搜索, query: {}, topK: {}", query, topK);

            // 直接调用 VectorStore，它会自动：
            // 1. 用配置的 EmbeddingModel 将 query 转为向量
            // 2. 在 Milvus 中执行相似度搜索
            // 3. 返回 Document 列表
            List<Document> documents = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(query)
                            .topK(topK > 0 ? topK : defaultTopK)
                            .similarityThreshold(defaultMinScore)
                            .build()
            );

            // 将 Document 转换为 Map（保持接口兼容）
            return documents.stream()
                    .map(this::convertToMap)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("向量搜索失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public SkillResult getSkillResult(String query, String skillType) {
        try {
            log.info("获取技能结果, query: {}, skillType: {}", query, skillType);

            // 执行搜索
            List<Document> documents = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(query)
                            .topK(defaultTopK)
                            .similarityThreshold(defaultMinScore)
                            .build()
            );

            // 转换为 SkillResult.Reference
            List<SkillResult.Reference> references = documents.stream()
                    .map(doc -> SkillResult.Reference.builder()
                            .title(doc.getMetadata().getOrDefault("title", "未知").toString())
                            .content(doc.getText())
                            .source(doc.getMetadata().getOrDefault("source", "vector_store").toString())
                            .score(doc.getMetadata().containsKey("distance") ?
                                    1 - (double) doc.getMetadata().get("distance") : 0.8)
                            .metadata(doc.getMetadata())
                            .build())
                    .collect(Collectors.toList());

            double confidence = references.isEmpty() ? 0 :
                    references.stream().mapToDouble(SkillResult.Reference::getScore).max().orElse(0);

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
            log.error("获取技能结果失败", e);
            return SkillResult.builder()
                    .skillName(skillType)
                    .references(new ArrayList<>())
                    .confidence(Double.valueOf(0))
                    .metadata(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @Override
    public void addResource(String id, String type, String name, String description,
                            List<String> keywords, Map<String, Object> metadata) {
        try {
            // 构建文档内容
            String content = String.format("类型：%s，名称：%s，描述：%s，关键词：%s",
                    type, name, description, String.join(",", keywords));

            // 构建元数据
            Map<String, Object> docMetadata = new HashMap<>(metadata);
            docMetadata.put("id", id);
            docMetadata.put("resource_type", type);
            docMetadata.put("name", name);
            docMetadata.put("keywords", keywords);

            // 创建 Document
            Document document = new Document(id, content, docMetadata);

            // 添加到 VectorStore
            vectorStore.add(List.of(document));

            log.info("向量资源添加成功, id: {}", id);

        } catch (Exception e) {
            log.error("添加向量资源失败", e);
            throw new RuntimeException("添加向量资源失败", e);
        }
    }

    @Override
    public void batchAddResources(List<Map<String, Object>> resources) {
        try {
            List<Document> documents = new ArrayList<>();

            for (Map<String, Object> resource : resources) {
                String content = String.format("类型：%s，名称：%s，描述：%s，关键词：%s",
                        resource.get("resource_type"),
                        resource.get("name"),
                        resource.get("description"),
                        String.join(",", (List<String>) resource.get("keywords")));

                String id = (String) resource.getOrDefault("id", UUID.randomUUID().toString());

                Document doc = new Document(id, content, resource);
                documents.add(doc);
            }

            vectorStore.add(documents);
            log.info("批量添加向量资源完成, 数量: {}", documents.size());

        } catch (Exception e) {
            log.error("批量添加向量资源失败", e);
            throw new RuntimeException("批量添加向量资源失败", e);
        }
    }

    @Override
    public void updateWeight(String id, double weight) {
        // VectorStore 不直接支持更新权重
        // 可以通过更新元数据实现
        log.warn("VectorStore 不直接支持更新权重，可通过重新添加文档实现");
    }

    /**
     * 将 Document 转换为 Map（保持接口兼容）
     */
    private Map<String, Object> convertToMap(Document doc) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", doc.getId());
        result.put("content", doc.getText());
        result.put("similarity", doc.getMetadata().getOrDefault("distance", 0));
        result.putAll(doc.getMetadata());
        return result;
    }
}