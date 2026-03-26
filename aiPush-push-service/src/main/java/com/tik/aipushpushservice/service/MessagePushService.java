package com.tik.aipushpushservice.service;



import com.tik.aipushpushservice.bean.MessageLog;

import java.util.Map;

public interface MessagePushService {

    /**
     * 推送消息
     */
    String pushMessage(String businessData, String targetPlatform, String summaryType);

    /**
     * 异步推送消息
     */
    void asyncPushMessage(String businessData, String targetPlatform, String summaryType);

    /**
     * 根据追踪ID查询推送结果
     */
    MessageLog queryResult(String traceId);

    /**
     * 重试失败的消息
     */
    int retryFailedMessages();

    /**
     * 推送统计
     */
    Map<String, Object> pushStatistics(String startTime, String endTime);
}
