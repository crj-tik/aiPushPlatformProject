package com.tik.aipushpushservice.bean;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageLog {
    private Long id;
    private String traceId;
    private String userId;
    private String userMessage;
    private String matchedResourceId;
    private String matchedResourceName;
    private Double similarityScore;
    private String summary;
    private String targetPlatform;
    private Integer pushStatus;  // 0-待推送,1-成功,2-失败
    private String errorMessage;
    private Map<String, Object> context;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}