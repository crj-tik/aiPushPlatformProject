package com.tik.aipushpushservice.skill;


import com.tik.aipushpushservice.bean.SkillResult;
import com.tik.aipushpushservice.service.AISummaryService;
import com.tik.aipushpushservice.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataQuerySkill implements Skill {

    private final JdbcTemplate jdbcTemplate;
    private final AISummaryService aiSummaryService;

    @Override
    public boolean match(String message, Map<String, Object> context) {
        String lowerMsg = message.toLowerCase();
        return lowerMsg.contains("查询") ||
                lowerMsg.contains("数据") ||
                lowerMsg.contains("统计") ||
                lowerMsg.contains("多少") ||
                lowerMsg.contains("数量");
    }

    @Override
    public SkillResult execute(String message, Map<String, Object> context) {
        try {
            // 1. 提取查询参数
            Map<String, Object> params = aiSummaryService.extractParams(message, "data_query");

            // 2. 执行查询
            List<Map<String, Object>> data = executeQuery(params);

            // 3. 构建参考信息
            List<SkillResult.Reference> references = new ArrayList<>();
            for (Map<String, Object> row : data) {
                SkillResult.Reference ref = SkillResult.Reference.builder()
                        .title("数据库查询结果")
                        .content(JsonUtils.toJson(row))
                        .source("business_db")
                        .score(1.0)
                        .metadata(Map.of("table", params.get("table")))
                        .build();
                references.add(ref);
            }

            return SkillResult.builder()
                    .skillName(getName())
                    .references(references)
                    .confidence(data.isEmpty() ? 0 : 0.9)
                    .metadata(Map.of(
                            "rowCount", data.size(),
                            "params", params
                    ))
                    .build();

        } catch (Exception e) {
            log.error("数据查询技能执行失败", e);
            return SkillResult.builder()
                    .skillName(getName())
                    .references(new ArrayList<>())
                    .confidence(Double.valueOf(0))
                    .metadata(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @Override
    public String getName() {
        return "data_query";
    }

    @Override
    public String getDescription() {
        return "数据查询技能，支持从数据库查询业务数据";
    }

    @Override
    public int getPriority() {
        return 100;
    }

    /**
     * 执行查询
     */
    private List<Map<String, Object>> executeQuery(Map<String, Object> params) {
        String table = (String) params.get("table");
        if (table == null) {
            table = "business_data";
        }

        // 简单查询示例
        String sql = "SELECT * FROM " + table + " LIMIT 10";
        return jdbcTemplate.queryForList(sql);
    }
}
