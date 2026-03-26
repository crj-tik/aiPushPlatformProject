package com.tik.aipushpushservice.service;

import com.tik.aipushpushservice.bean.SkillResult;
import com.tik.aipushpushservice.skill.Skill;

import java.util.List;
import java.util.Map;

public interface SkillManagerService {

    /**
     * 注册技能
     */
    void registerSkill(Skill skill);

    /**
     * 执行技能
     */
    SkillResult executeSkill(String message, String skillType);

    /**
     * 匹配技能
     */
    Skill matchSkill(String message, Map<String, Object> context);

    /**
     * 获取所有技能
     */
    List<Skill> getAllSkills();

    /**
     * 获取技能
     */
    Skill getSkill(String skillName);
}