package com.tik.aipushpushservice.skill;


import com.tik.aipushpushservice.bean.SkillResult;

import java.util.Map;

public interface Skill {

    /**
     * 判断是否匹配
     */
    boolean match(String message, Map<String, Object> context);

    /**
     * 执行技能
     */
    SkillResult execute(String message, Map<String, Object> context);

    /**
     * 获取技能名称
     */
    String getName();

    /**
     * 获取技能描述
     */
    String getDescription();

    /**
     * 获取优先级
     */
    default int getPriority() {
        return 0;
    }
}