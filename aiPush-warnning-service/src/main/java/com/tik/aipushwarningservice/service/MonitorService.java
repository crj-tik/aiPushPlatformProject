package com.tik.aipushwarningservice.service;

import com.github.pagehelper.PageInfo;
import com.tik.aipushwarningservice.bean.MonitorRecord;
import com.tik.aipushwarningservice.bean.MonitorRecordDTO;
import com.tik.aipushwarningservice.bean.RiskAnalysisDTO;

import java.util.List;
import java.util.Map;

public interface MonitorService {

    /**
     * 保存监控记录
     */
    MonitorRecord saveRecord(MonitorRecord record);

    /**
     * 保存AI分析结果
     */
    MonitorRecord saveAnalysisResult(RiskAnalysisDTO analysisDTO);

    /**
     * 更新分析结果
     */
    MonitorRecord updateAnalysisResult(Long id, String analysisResult, String riskLevel);

    /**
     * 根据ID查询
     */
    MonitorRecord getById(Long id);

    /**
     * 根据人员ID查询记录
     */
    List<MonitorRecord> getByPersonId(Long personId);

    /**
     * 获取人员最近的分析历史
     */
    List<MonitorRecord> getPersonAnalysisHistory(Long personId, int limit);

    /**
     * 查询高风险记录
     */
    List<MonitorRecord> getHighRiskRecords();

    /**
     * 根据部门查询高风险记录
     */
    List<MonitorRecord> getHighRiskByDepartment(String department);

    /**
     * 分页查询监控记录
     */
    PageInfo<MonitorRecord> pageQuery(MonitorRecordDTO dto);

    /**
     * 获取今日记录数
     */
    Long getTodayCount();

    /**
     * 获取风险等级分布
     */
    Map<String, Long> getRiskLevelDistribution();

    /**
     * 获取部门风险统计
     */
    List<Map<String, Object>> getDepartmentRiskStats();

    /**
     * 逻辑删除记录
     */
    boolean deleteRecord(Long id, String operator);
}