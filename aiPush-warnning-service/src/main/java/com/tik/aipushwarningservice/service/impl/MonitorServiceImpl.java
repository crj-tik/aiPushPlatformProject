package com.tik.aipushwarningservice.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tik.aipushwarningservice.bean.*;
import com.tik.aipushwarningservice.mapper.MonitorMapper;
import com.tik.aipushwarningservice.service.MonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorServiceImpl implements MonitorService {

    private final MonitorMapper monitorMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MonitorRecord saveRecord(MonitorRecord record) {
        log.info("保存监控记录: personId={}, riskLevel={}",
                record.getPersonId(), record.getRiskLevel());

        record.setCreateTime(LocalDateTime.now());
        record.setDeleted(0);
        monitorMapper.insert(record);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MonitorRecord saveAnalysisResult(RiskAnalysisDTO analysisDTO) {
        MonitorRecord record = new MonitorRecord();
        record.setPersonId(analysisDTO.getPersonId());
        record.setPersonName(analysisDTO.getPersonName());
        record.setPersonIdCard(analysisDTO.getPersonIdCard());
        record.setDepartment(analysisDTO.getDepartment());
        record.setPosition(analysisDTO.getPosition());
        record.setAnalysisType(analysisDTO.getAnalysisType());
        record.setAnalysisContent(analysisDTO.getAnalysisContent());
        record.setAnalysisResult(analysisDTO.getAnalysisResult());
        record.setRiskLevel(analysisDTO.getRiskLevel());
        record.setRiskScore(analysisDTO.getRiskScore());
        record.setSuggestions(analysisDTO.getSuggestions());
        record.setCreateTime(LocalDateTime.now());
        record.setCreateBy("SYSTEM");
        record.setDeleted(0);

        monitorMapper.insert(record);
        log.info("AI分析结果已保存: id={}, riskLevel={}", record.getId(), record.getRiskLevel());
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MonitorRecord updateAnalysisResult(Long id, String analysisResult, String riskLevel) {
        MonitorRecord record = new MonitorRecord();
        record.setId(id);
        record.setAnalysisResult(analysisResult);
        record.setRiskLevel(riskLevel);
        record.setUpdateTime(LocalDateTime.now());
        record.setUpdateBy("SYSTEM");

        monitorMapper.updateById(record);
        return monitorMapper.selectById(id);
    }

    @Override
    public MonitorRecord getById(Long id) {
        return monitorMapper.selectById(id);
    }

    @Override
    public List<MonitorRecord> getByPersonId(Long personId) {
        return monitorMapper.selectByPersonId(personId);
    }

    @Override
    public List<MonitorRecord> getPersonAnalysisHistory(Long personId, int limit) {
        return monitorMapper.selectRecentByPersonId(personId, limit);
    }

    @Override
    public List<MonitorRecord> getHighRiskRecords() {
        return monitorMapper.selectByRiskLevel("HIGH");
    }

    @Override
    public List<MonitorRecord> getHighRiskByDepartment(String department) {
        return monitorMapper.selectHighRiskByDepartment(department);
    }

    @Override
    public PageInfo<MonitorRecord> pageQuery(MonitorRecordDTO dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<MonitorRecord> records = monitorMapper.selectByCondition(dto);
        return new PageInfo<>(records);
    }

    @Override
    public Long getTodayCount() {
        return monitorMapper.countTodayRecords();
    }

    @Override
    public Map<String, Long> getRiskLevelDistribution() {
        List<RiskLevelStat> stats = monitorMapper.countByRiskLevel();
        return stats.stream()
                .collect(Collectors.toMap(
                        RiskLevelStat::getRiskLevel,
                        RiskLevelStat::getCount
                ));
    }

    @Override
    public List<Map<String, Object>> getDepartmentRiskStats() {
        List<DepartmentRiskStat> stats = monitorMapper.countByDepartment();
        return stats.stream()
                .map(stat -> {
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("department", stat.getDepartment());
                    result.put("highRiskCount", stat.getHighRiskCount());
                    result.put("mediumRiskCount", stat.getMediumRiskCount());
                    result.put("lowRiskCount", stat.getLowRiskCount());
                    return result;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRecord(Long id, String operator) {
        int result = monitorMapper.deleteById(id, operator);
        return result > 0;
    }
}
