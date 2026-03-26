package com.tik.aipushwarningservice.bean;

import lombok.Data;

@Data
public class DepartmentRiskStat {
    private String department;
    private Long highRiskCount;
    private Long mediumRiskCount;
    private Long lowRiskCount;
    // getters and setters
}
