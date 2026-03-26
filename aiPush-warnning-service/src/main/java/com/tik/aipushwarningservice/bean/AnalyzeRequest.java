package com.tik.aipushwarningservice.bean;

import lombok.Data;

@Data
public class AnalyzeRequest {
    private Long personId;
    private String personName;
    private String department;
    private String position;
    private String workData;
}