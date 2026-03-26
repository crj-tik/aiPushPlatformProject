package com.tik.aipushwarningservice.mapper;
import com.tik.aipushwarningservice.bean.DepartmentRiskStat;
import com.tik.aipushwarningservice.bean.MonitorRecord;
import com.tik.aipushwarningservice.bean.MonitorRecordDTO;
import com.tik.aipushwarningservice.bean.RiskLevelStat;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MonitorMapper {

    // ============== 基础CRUD ==============

    /**
     * 插入监控记录
     */
    int insert(MonitorRecord record);

    /**
     * 批量插入监控记录
     */
    int batchInsert(@Param("list") List<MonitorRecord> records);

    /**
     * 根据ID更新记录
     */
    int updateById(MonitorRecord record);

    /**
     * 逻辑删除记录
     */
    int deleteById(@Param("id") Long id, @Param("updateBy") String updateBy);

    /**
     * 物理删除记录（慎用）
     */
    int physicalDelete(@Param("id") Long id);

    /**
     * 根据ID查询
     */
    MonitorRecord selectById(@Param("id") Long id);

    /**
     * 查询所有记录
     */
    List<MonitorRecord> selectAll();


    // ============== 条件查询 ==============

    /**
     * 根据人员ID查询
     */
    List<MonitorRecord> selectByPersonId(@Param("personId") Long personId);

    /**
     * 根据人员ID和风险等级查询
     */
    List<MonitorRecord> selectByPersonIdAndRiskLevel(
            @Param("personId") Long personId,
            @Param("riskLevel") String riskLevel);

    /**
     * 根据风险等级查询
     */
    List<MonitorRecord> selectByRiskLevel(@Param("riskLevel") String riskLevel);

    /**
     * 根据风险等级查询（带分页）
     */
    List<MonitorRecord> selectByRiskLevelWithPage(MonitorRecordDTO dto);

    /**
     * 根据部门查询高风险记录
     */
    List<MonitorRecord> selectHighRiskByDepartment(@Param("department") String department);

    /**
     * 根据分析类型查询
     */
    List<MonitorRecord> selectByAnalysisType(@Param("analysisType") String analysisType);


    // ============== 统计分析 ==============

    /**
     * 统计今日新增监控记录数
     */
    Long countTodayRecords();

    /**
     * 统计各风险等级数量
     */
    List<RiskLevelStat> countByRiskLevel();

    /**
     * 统计各部门风险分布
     */
    List<DepartmentRiskStat> countByDepartment();

    /**
     * 查询最新10条高风险记录
     */
    List<MonitorRecord> selectLatestHighRisk();

    /**
     * 查询人员最近的分析记录
     */
    List<MonitorRecord> selectRecentByPersonId(
            @Param("personId") Long personId,
            @Param("limit") Integer limit);

    /**
     * 根据时间范围查询
     */
    List<MonitorRecord> selectByTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 条件查询（动态SQL）
     */
    List<MonitorRecord> selectByCondition(MonitorRecordDTO dto);

    /**
     * 统计条件查询的总数
     */
    Long countByCondition(MonitorRecordDTO dto);

}
