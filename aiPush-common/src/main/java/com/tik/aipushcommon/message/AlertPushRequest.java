package com.tik.aipushcommon.message;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class AlertPushRequest {
    private String traceId;
    private Long personId;
    private String personName;
    private String idcard;
    private String department;
    private String position;
    private String alertType;
    private String alertLevel;
    private Integer alertScore;
    private String alertTitle;
    private String alertContent;
    private String analysisResult;
    private String suggestions;
    private LocalDateTime alertTime;
    private Map<String, Object> ext;
}
