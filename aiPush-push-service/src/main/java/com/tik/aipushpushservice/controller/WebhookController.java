package com.tik.aipushpushservice.controller;

import com.tik.aipushpushservice.service.SkillManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final SkillManagerService skillManagerService;

    /**
     * 接收飞书消息
     */
    @PostMapping("/feishu")
    public Map<String, Object> handleFeishu(@RequestBody Map<String, Object> request) {
        try {
            log.info("收到飞书消息: {}", request);

            // 解析消息内容
            Map<String, Object> event = (Map<String, Object>) request.get("event");
            Map<String, Object> message = (Map<String, Object>) event.get("message");
            String content = (String) message.get("content");

            // 执行技能
            var skillResult = skillManagerService.executeSkill(content, "knowledge_base");

            // 构建回复
            return Map.of(
                    "msg_type", "text",
                    "content", Map.of("text", skillResult.getReferences().toString())
            );

        } catch (Exception e) {
            log.error("处理飞书消息失败", e);
            return Map.of(
                    "msg_type", "text",
                    "content", Map.of("text", "处理失败: " + e.getMessage())
            );
        }
    }

    /**
     * 接收企业微信消息
     */
    @PostMapping("/wechat")
    public Map<String, Object> handleWechat(@RequestBody Map<String, Object> request) {
        try {
            log.info("收到企业微信消息: {}", request);

            Map<String, Object> text = (Map<String, Object>) request.get("text");
            String content = (String) text.get("content");

            var skillResult = skillManagerService.executeSkill(content, "knowledge_base");

            return Map.of(
                    "msgtype", "markdown",
                    "markdown", Map.of("content", skillResult.getReferences().toString())
            );

        } catch (Exception e) {
            log.error("处理企业微信消息失败", e);
            return Map.of(
                    "msgtype", "text",
                    "text", Map.of("content", "处理失败")
            );
        }
    }

    /**
     * 接收钉钉消息
     */
    @PostMapping("/dingtalk")
    public Map<String, Object> handleDingtalk(@RequestBody Map<String, Object> request) {
        try {
            log.info("收到钉钉消息: {}", request);

            Map<String, Object> text = (Map<String, Object>) request.get("text");
            String content = (String) text.get("content");

            var skillResult = skillManagerService.executeSkill(content, "knowledge_base");

            return Map.of(
                    "msgtype", "markdown",
                    "markdown", Map.of(
                            "title", "智能回复",
                            "text", skillResult.getReferences().toString()
                    )
            );

        } catch (Exception e) {
            log.error("处理钉钉消息失败", e);
            return Map.of(
                    "msgtype", "text",
                    "text", Map.of("content", "处理失败")
            );
        }
    }
}