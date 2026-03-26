package com.tik.aipushpushservice.bean;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillResult {
    private String skillName;
    private List<Reference> references;
    private Map<String, Object> metadata;
    private Double confidence;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reference {
        private String title;
        private String content;
        private String source;
        private Double score;
        private Map<String, Object> metadata;
    }
}
