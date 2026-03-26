package com.tik.aipushpushservice.utils;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmbeddingUtils {

    private final EmbeddingModel embeddingModel;

    @Value("${spring.ai.dashscope.embedding.options.model:text-embedding-v4}")
    private String embeddingModelName;

    @Value("${vector.dimensions:1536}")
    private Integer embeddingDimensions;

    public float[] getEmbedding(String text) {
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

        return new float[embeddingDimensions];
    }

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
