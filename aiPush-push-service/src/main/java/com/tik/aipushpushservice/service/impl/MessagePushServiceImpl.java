package com.tik.aipushpushservice.service.impl;


import com.tik.aipushpushservice.bean.MessageLog;
import com.tik.aipushpushservice.bean.SkillResult;
import com.tik.aipushpushservice.config.WebhookConfig;
import com.tik.aipushpushservice.mapper.MessageLogMapper;
import com.tik.aipushpushservice.service.AISummaryService;
import com.tik.aipushpushservice.service.MessagePushService;
import com.tik.aipushpushservice.service.SkillManagerService;
import com.tik.aipushpushservice.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagePushServiceImpl implements MessagePushService {

    private final AISummaryService aiSummaryService;
    private final SkillManagerService skillManagerService;
    private final MessageLogMapper messageLogMapper;
    private final WebhookConfig webhookConfig;
    private final RestTemplate restTemplate;

    @Value("${vector.top-k:5}")
    private int topK;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String pushMessage(String businessData, String targetPlatform, String summaryType) {
        String traceId = UUID.randomUUID().toString();

        try {
            log.info("开始推送消息, traceId: {}, platform: {}, type: {}", traceId, targetPlatform, summaryType);

            // 1. 解析数据
            Map<String, Object> data = JsonUtils.toMap(businessData);

            // 2. 技能匹配
            SkillResult skillResult = skillManagerService.executeSkill(businessData, summaryType);

            // 3. AI总结
            String summary = aiSummaryService.summarize(data, summaryType, skillResult);

            // 4. 推送消息
            boolean pushSuccess = pushToPlatform(targetPlatform, summary);

            // 5. 记录日志
            MessageLog messageLog = MessageLog.builder()
                    .traceId(traceId)
                    .userMessage(businessData)
                    .matchedResourceId(skillResult.getSkillName())
                    .matchedResourceName(skillResult.getSkillName())
                    .similarityScore(skillResult.getConfidence())
                    .summary(summary)
                    .targetPlatform(targetPlatform)
                    .pushStatus(pushSuccess ? 1 : 2)
                    .errorMessage(pushSuccess ? null : "推送失败")
                    .context(Map.of("skillResult", skillResult))
                    .createdAt(LocalDateTime.now())
                    .build();

            messageLogMapper.insert(messageLog);

            log.info("消息推送完成, traceId: {}, success: {}", traceId, pushSuccess);
            return traceId;

        } catch (Exception e) {
            log.error("消息推送失败, traceId: {}", traceId, e);

            // 记录失败日志
            MessageLog messageLog = MessageLog.builder()
                    .traceId(traceId)
                    .userMessage(businessData)
                    .targetPlatform(targetPlatform)
                    .pushStatus(2)
                    .errorMessage(e.getMessage())
                    .createdAt(LocalDateTime.now())
                    .build();
            messageLogMapper.insert(messageLog);

            throw new RuntimeException("消息推送失败", e);
        }
    }

    @Override
    @Async("messagePushExecutor")
    public void asyncPushMessage(String businessData, String targetPlatform, String summaryType) {
        try {
            pushMessage(businessData, targetPlatform, summaryType);
        } catch (Exception e) {
            log.error("异步推送失败", e);
        }
    }

    @Override
    public MessageLog queryResult(String traceId) {
        return messageLogMapper.selectByTraceId(traceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int retryFailedMessages() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(1);

        List<MessageLog> failedLogs = messageLogMapper.selectFailedLogs(startTime, endTime);
        int successCount = 0;

        for (MessageLog messageLog : failedLogs) {
            try {
                boolean pushSuccess = pushToPlatform(messageLog.getTargetPlatform(), messageLog.getSummary());
                if (pushSuccess) {
                    messageLogMapper.updatePushStatus(messageLog.getId(), 1, null);
                    successCount++;
                }
            } catch (Exception e) {
                log.error("重试失败", e);
            }
        }

        return successCount;
    }

    @Override
    public Map<String, Object> pushStatistics(String startTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);

        return messageLogMapper.statistics(start, end);
    }

    /**
     * 推送到指定平台
     */
    private boolean pushToPlatform(String platform, String content) {
        try {
            String webhookUrl = getWebhookUrl(platform);
            if (webhookUrl == null) {
                throw new IllegalArgumentException("不支持的平台: " + platform);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = buildRequestBody(platform, content);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            Map<String, Object> response = restTemplate.postForObject(webhookUrl, request, Map.class);

            log.info("推送响应: {}", response);
            return isPushSuccessful(platform, response);

        } catch (Exception e) {
            log.error("推送失败", e);
            return false;
        }
    }

    /**
     * 获取Webhook地址
     */
    private String getWebhookUrl(String platform) {
        switch (platform.toLowerCase()) {
            case "wechat":
                return webhookConfig.getWechat();
            case "feishu":
                return webhookConfig.getFeishu();
            case "dingtalk":
                return webhookConfig.getDingtalk();
            default:
                return null;
        }
    }

    /**
     * 构建请求体
     */
    private Map<String, Object> buildRequestBody(String platform, String content) {
        Map<String, Object> body = new HashMap<>();

        switch (platform.toLowerCase()) {
            case "wechat":
                body.put("msgtype", "markdown");
                body.put("markdown", Map.of("content", content));
                break;
            case "feishu":
                body.put("msg_type", "text");
                body.put("content", Map.of("text", content));
                break;
            case "dingtalk":
                body.put("msgtype", "markdown");
                body.put("markdown", Map.of(
                        "title", "消息推送",
                        "text", content
                ));
                break;
        }

        return body;
    }

    private boolean isPushSuccessful(String platform, Map<String, Object> response) {
        if (response == null) {
            return false;
        }

        return switch (platform.toLowerCase()) {
            case "wechat", "dingtalk" ->
                    Integer.valueOf(0).equals(response.get("errcode"));
            case "feishu" ->
                    Integer.valueOf(0).equals(response.get("code")) ||
                            "success".equalsIgnoreCase(String.valueOf(response.get("msg")));
            default -> false;
        };
    }
}
