package com.tik.aipushpushservice.service.impl;

import com.tik.aipushpushservice.bean.SkillResult;
import com.tik.aipushpushservice.service.SkillManagerService;
import com.tik.aipushpushservice.service.VectorSearchService;
import com.tik.aipushpushservice.skill.Skill;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillManagerServiceImpl implements SkillManagerService {

    private final VectorSearchService vectorSearchService;
    private final Map<String, Skill> skillMap = new ConcurrentHashMap<>();
    private final List<Skill> skills;  // Spring会自动注入所有Skill实现

    @PostConstruct
    public void init() {
        for (Skill skill : skills) {
            registerSkill(skill);
            log.info("注册技能: {}", skill.getName());
        }
    }

    @Override
    public void registerSkill(Skill skill) {
        skillMap.put(skill.getName(), skill);
    }

    @Override
    public SkillResult executeSkill(String message, String skillType) {
        // 先通过向量搜索获取参考信息
        SkillResult searchResult = vectorSearchService.getSkillResult(message, skillType);

        // 获取具体技能
        Skill skill = skillMap.get(skillType);
        if (skill != null) {
            // 执行技能
            SkillResult skillResult = skill.execute(message, Map.of("searchResult", searchResult));

            // 合并结果
            if (searchResult.getReferences() != null) {
                if (skillResult.getReferences() == null) {
                    skillResult.setReferences(searchResult.getReferences());
                } else {
                    skillResult.getReferences().addAll(searchResult.getReferences());
                }
            }

            return skillResult;
        }

        // 如果没有找到具体技能，返回搜索结果
        return searchResult;
    }

    @Override
    public Skill matchSkill(String message, Map<String, Object> context) {
        // 按优先级匹配技能
        for (Skill skill : skills) {
            if (skill.match(message, context)) {
                log.info("匹配到技能: {}", skill.getName());
                return skill;
            }
        }

        // 默认返回第一个技能
        return skills.isEmpty() ? null : skills.get(0);
    }

    @Override
    public List<Skill> getAllSkills() {
        return skills;
    }

    @Override
    public Skill getSkill(String skillName) {
        return skillMap.get(skillName);
    }
}