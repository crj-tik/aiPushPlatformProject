package com.tik.aipushpushservice.skill;

import com.tik.aipushpushservice.bean.SkillResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertAnalysisSkill implements Skill {

    @Override
    public boolean match(String message, Map<String, Object> context) {
        String lowerMsg = message.toLowerCase();
        return lowerMsg.contains("告警") ||
                lowerMsg.contains("报警") ||
                lowerMsg.contains("异常") ||
                lowerMsg.contains("故障") ||
                lowerMsg.contains("错误");
    }

    @Override
    public SkillResult execute(String message, Map<String, Object> context) {
        try {
            // 模拟获取告警信息
            List<Map<String, Object>> alerts = getRecentAlerts();

            // 构建参考信息
            List<SkillResult.Reference> references = new ArrayList<>();
            for (Map<String, Object> alert : alerts) {
                SkillResult.Reference ref = SkillResult.Reference.builder()
                        .title("告警: " + alert.get("title"))
                        .content((String) alert.get("description"))
                        .source("monitoring_system")
                        .score((Double) alert.getOrDefault("level", 0.8))
                        .metadata(Map.of(
                                "time", alert.get("time"),
                                "level", alert.get("level")
                        ))
                        .build();
                references.add(ref);
            }

            return SkillResult.builder()
                    .skillName(getName())
                    .references(references)
                    .confidence(alerts.isEmpty() ? 0 : 0.85)
                    .metadata(Map.of("alertCount", alerts.size()))
                    .build();

        } catch (Exception e) {
            log.error("告警分析技能执行失败", e);
            return SkillResult.builder()
                    .skillName(getName())
                    .references(List.of())
                    .confidence(Double.valueOf(0))
                    .metadata(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @Override
    public String getName() {
        return "alert_analysis";
    }

    @Override
    public String getDescription() {
        return "告警分析技能";
    }

    @Override
    public int getPriority() {
        return 80;
    }

    /**
     * 模拟获取最近的告警信息
     */
    private List<Map<String, Object>> getRecentAlerts() {
        List<Map<String, Object>> alerts = new ArrayList<>();

        Map<String, Object> alert1 = Map.of(
                "title", "订单服务响应超时",
                "description", "订单服务在过去5分钟内响应时间超过3秒，影响订单查询接口",
                "level", 0.9,
                "time", System.currentTimeMillis() - 300000
        );

        Map<String, Object> alert2 = Map.of(
                "title", "数据库连接池使用率过高",
                "description", "数据库连接池使用率达到85%，建议检查慢查询",
                "level", 0.7,
                "time", System.currentTimeMillis() - 600000
        );

        alerts.add(alert1);
        alerts.add(alert2);

        return alerts;
    }
}
