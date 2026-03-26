package com.tik.aipushwarningservice.bean;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MonitorRecord {
    private Long id;
    private Long personId;
    private String personName;
    private String personIdCard;
    private String department;
    private String position;
    private String analysisType;      // 分析类型：工作评估/风险分析/离职预测
    private String analysisContent;    // 分析内容
    private String analysisResult;     // AI分析结果
    private String riskLevel;          // 风险等级：HIGH/MEDIUM/LOW
    private Integer riskScore;         // 风险分数 0-100
    private String suggestions;        // 改进建议
    private String aiModel;            // 使用的AI模型
    private Integer promptTokens;       // 输入token数
    private Integer completionTokens;   // 输出token数
    private Integer totalTokens;        // 总token数
    private LocalDateTime createTime;
    private String createBy;
    private LocalDateTime updateTime;
    private String updateBy;
    private Integer deleted;            // 逻辑删除 0-未删除 1-已删除
}
