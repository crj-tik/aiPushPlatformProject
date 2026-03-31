package com.tik.aipushpushservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tik.aipushcommon.message.AlertPushSummaryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertPushSummaryProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic.alert-push-summary:alert-push-summary}")
    private String alertPushSummaryTopic;

    public void send(AlertPushSummaryMessage message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(alertPushSummaryTopic, buildPartitionKey(message), payload);
            log.info("已发送告警总结消息, traceId={}, personId={}, topic={}",
                    message.getTraceId(), message.getPersonId(), alertPushSummaryTopic);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("序列化告警总结消息失败", e);
        }
    }

    private String buildPartitionKey(AlertPushSummaryMessage message) {
        if (message.getIdcard() != null && !message.getIdcard().isBlank()) {
            return message.getIdcard();
        }
        if (message.getPersonId() != null) {
            return String.valueOf(message.getPersonId());
        }
        return message.getTraceId();
    }
}
