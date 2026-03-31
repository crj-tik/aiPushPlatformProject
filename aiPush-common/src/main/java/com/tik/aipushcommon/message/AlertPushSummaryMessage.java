package com.tik.aipushcommon.message;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class AlertPushSummaryMessage {
    private String traceId;
    private Long personId;
    private String personName;
    private String idcard;
    private String alertType;
    private String alertLevel;
    private Integer alertScore;
    private String alertTitle;
    private String originalContent;
    private String analysisResult;
    private String suggestions;
    private String aiSummary;
    private LocalDateTime alertTime;
    private LocalDateTime summarizedAt;
    private Map<String, Object> ext;
}
