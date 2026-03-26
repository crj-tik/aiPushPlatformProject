package com.tik.aipushwarningservice.service;

import com.tik.aipushwarningservice.bean.MonitorRecord;
import com.tik.aipushwarningservice.bean.RiskAnalysisDTO;
import java.util.List;
import reactor.core.publisher.Flux;

public interface AiAnalysisService {

    /**
     * 分析人员风险（同步）
     */
    RiskAnalysisDTO analyzePersonRisk(Long personId, String personName,
                                      String department, String position,
                                      String workData);

    /**
     * 分析人员风险（流式）
     */
    Flux<String> analyzePersonRiskStream(Long personId);

    /**
     * 保存分析结果
     */
    void saveAnalysisResult(RiskAnalysisDTO analysisDTO);

    /**
     * 获取人员分析历史
     */
    List<MonitorRecord> getPersonAnalysisHistory(Long personId);
}