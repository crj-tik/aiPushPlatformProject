package com.tik.aipushwarningservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tik.aipushcommon.message.AlertPushRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertPushProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic.alert-push-request:alert-push-request}")
    private String alertPushRequestTopic;

    public void send(AlertPushRequest request) {
        try {
            String payload = objectMapper.writeValueAsString(request);
            kafkaTemplate.send(alertPushRequestTopic, buildPartitionKey(request), payload);
            log.info("已发送待推送告警消息, traceId={}, personId={}, topic={}",
                    request.getTraceId(), request.getPersonId(), alertPushRequestTopic);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("序列化待推送告警消息失败", e);
        }
    }

    private String buildPartitionKey(AlertPushRequest request) {
        if (request.getIdcard() != null && !request.getIdcard().isBlank()) {
            return request.getIdcard();
        }
        if (request.getPersonId() != null) {
            return String.valueOf(request.getPersonId());
        }
        return request.getTraceId();
    }
}
