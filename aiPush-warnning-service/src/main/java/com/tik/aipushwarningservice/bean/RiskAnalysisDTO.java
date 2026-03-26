package com.tik.aipushwarningservice.bean;


import lombok.Data;

@Data
public class RiskAnalysisDTO {
    private Long personId;
    private String personName;
    private String analysisType;
    private String riskLevel;
    private Integer riskScore;
    private String suggestions;
    private String analysisResult;
}