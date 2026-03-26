package com.tik.aipushwarningservice.bean;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MonitorRecordDTO {
    private Long id;
    private Long personId;
    private String personName;
    private String department;
    private String analysisType;
    private String riskLevel;
    private Integer riskScore;
    private LocalDateTime createTime;

    // 用于分页查询
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
