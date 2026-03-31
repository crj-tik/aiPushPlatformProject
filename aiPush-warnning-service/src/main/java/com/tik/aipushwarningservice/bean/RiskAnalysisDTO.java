package com.tik.aipushwarningservice.bean;


import lombok.Data;

@Data
public class RiskAnalysisDTO {
    private Long personId;
    private String personName;
    private String personIdCard;
    private String department;
    private String position;
    private String analysisType;
    private String riskLevel;
    private Integer riskScore;
    private String suggestions;
    private String analysisResult;
    private String analysisContent;
}
