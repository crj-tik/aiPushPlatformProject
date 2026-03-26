package com.tik.aipushpushservice.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EmbeddingUtils {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 调用OpenAI API获取文本向量
     */
    public float[] getEmbedding(String text) {
        Map<String, Object> request = new HashMap<>();
        request.put("input", text);
        request.put("model", "text-embedding-ada-002");

        Map<String, Object> response = restTemplate.postForObject(
                "https://api.openai.com/v1/embeddings",
                request,
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

        return new float[1536]; // 返回空向量
    }

    /**
     * 计算余弦相似度
     */
    public double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        if (normA == 0 || normB == 0) {
            return 0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
