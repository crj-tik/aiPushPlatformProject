package com.tik.aipushwarningservice.controller;

import com.tik.aipushwarningservice.bean.*;
import com.tik.aipushwarningservice.service.AiAnalysisService;
import com.tik.aipushwarningservice.service.MonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Slf4j  // 这个注解会自动生成 log 对象
@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {

    private final AiAnalysisService analysisService;
    private final MonitorService monitorService;

    /**
     * 分析人员风险
     */
    @PostMapping("/analyze")
    public Result<RiskAnalysisDTO> analyzePerson(@RequestBody AnalyzeRequest request) {
        log.info("接收到风险分析请求: personId={}, personName={}",
                request.getPersonId(), request.getPersonName());

        RiskAnalysisDTO analysis = analysisService.analyzePersonRisk(
                request.getPersonId(),
                request.getPersonName(),
                request.getDepartment(),
                request.getPosition(),
                request.getWorkData()
        );

        return Result.success(analysis);
    }

    /**
     * 流式分析（实时返回）
     */
    @GetMapping(value = "/analyze/stream/{personId}", produces = "text/event-stream")
    public Flux<String> analyzePersonStream(@PathVariable Long personId) {
        log.info("流式分析请求: personId={}", personId);

        // 这里调用AI流式接口
        return analysisService.analyzePersonRiskStream(personId);
    }

    /**
     * 获取人员的所有监控记录
     */
    @GetMapping("/records/{personId}")
    public Result<List<MonitorRecord>> getPersonRecords(@PathVariable Long personId) {
        log.info("查询人员监控记录: personId={}", personId);

        List<MonitorRecord> records = monitorService.getByPersonId(personId);
        return Result.success(records);
    }

    /**
     * 获取人员最近的分析历史
     */
    @GetMapping("/records/{personId}/recent")
    public Result<List<MonitorRecord>> getPersonRecentRecords(
            @PathVariable Long personId,
            @RequestParam(defaultValue = "5") int limit) {

        List<MonitorRecord> records = monitorService.getPersonAnalysisHistory(personId, limit);
        return Result.success(records);
    }

    /**
     * 获取所有高风险记录
     */
    @GetMapping("/high-risk")
    public Result<List<MonitorRecord>> getHighRiskRecords() {
        log.info("查询所有高风险记录");

        List<MonitorRecord> records = monitorService.getHighRiskRecords();
        return Result.success(records);
    }

    /**
     * 获取部门的高风险记录
     */
    @GetMapping("/high-risk/department/{department}")
    public Result<List<MonitorRecord>> getHighRiskByDepartment(@PathVariable String department) {
        log.info("查询部门高风险记录: department={}", department);

        List<MonitorRecord> records = monitorService.getHighRiskByDepartment(department);
        return Result.success(records);
    }

    /**
     * 分页查询监控记录
     */
    @PostMapping("/records/page")
    public Result<com.github.pagehelper.PageInfo<MonitorRecord>> pageQuery(
            @RequestBody MonitorRecordDTO queryDTO) {
        log.info("分页查询监控记录: pageNum={}, pageSize={}",
                queryDTO.getPageNum(), queryDTO.getPageSize());

        com.github.pagehelper.PageInfo<MonitorRecord> page = monitorService.pageQuery(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取今日统计
     */
    @GetMapping("/stats/today")
    public Result<Map<String, Object>> getTodayStats() {
        Long todayCount = monitorService.getTodayCount();
        Map<String, Long> riskLevelDist = monitorService.getRiskLevelDistribution();

        return Result.success(Map.of(
                "todayCount", todayCount,
                "riskLevelDistribution", riskLevelDist
        ));
    }

    /**
     * 获取风险等级分布
     */
    @GetMapping("/stats/risk-level")
    public Result<Map<String, Long>> getRiskLevelStats() {
        return Result.success(monitorService.getRiskLevelDistribution());
    }

    /**
     * 获取部门风险统计
     */
    @GetMapping("/stats/department")
    public Result<List<Map<String, Object>>> getDepartmentRiskStats() {
        return Result.success(monitorService.getDepartmentRiskStats());
    }

    /**
     * 根据ID查询记录
     */
    @GetMapping("/record/{id}")
    public Result<MonitorRecord> getRecordById(@PathVariable Long id) {
        MonitorRecord record = monitorService.getById(id);
        if (record == null) {
            return Result.error("记录不存在");
        }
        return Result.success(record);
    }

    /**
     * 删除记录（逻辑删除）
     */
    @DeleteMapping("/record/{id}")
    public Result<Boolean> deleteRecord(
            @PathVariable Long id,
            @RequestParam String operator) {

        boolean success = monitorService.deleteRecord(id, operator);
        if (success) {
            return Result.success(true);
        } else {
            return Result.error("删除失败");
        }
    }
}
