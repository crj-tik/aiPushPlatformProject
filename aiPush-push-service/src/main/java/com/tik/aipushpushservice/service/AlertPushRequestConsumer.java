package com.tik.aipushpushservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tik.aipushcommon.message.AlertPushRequest;
import com.tik.aipushcommon.message.AlertPushSummaryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertPushRequestConsumer {

    private final ObjectMapper objectMapper;
    private final AISummaryService aiSummaryService;
    private final AlertPushSummaryProducer alertPushSummaryProducer;

    @KafkaListener(
            topics = "${app.kafka.topic.alert-push-request:alert-push-request}",
            groupId = "${spring.kafka.consumer.group-id:push-alert-summary-group}"
    )
    public void consume(String payload) {
        try {
            AlertPushRequest request = objectMapper.readValue(payload, AlertPushRequest.class);
            String summary = aiSummaryService.summarize(buildSummaryData(request), "alert", null);

            AlertPushSummaryMessage message = new AlertPushSummaryMessage();
            message.setTraceId(request.getTraceId());
            message.setPersonId(request.getPersonId());
            message.setPersonName(request.getPersonName());
            message.setIdcard(request.getIdcard());
            message.setAlertType(request.getAlertType());
            message.setAlertLevel(request.getAlertLevel());
            message.setAlertScore(request.getAlertScore());
            message.setAlertTitle(request.getAlertTitle());
            message.setOriginalContent(request.getAlertContent());
            message.setAnalysisResult(request.getAnalysisResult());
            message.setSuggestions(request.getSuggestions());
            message.setAiSummary(summary);
            message.setAlertTime(request.getAlertTime());
            message.setSummarizedAt(LocalDateTime.now());
            message.setExt(request.getExt());

            alertPushSummaryProducer.send(message);
            log.info("已完成告警总结, traceId={}, personId={}", request.getTraceId(), request.getPersonId());
        } catch (Exception e) {
            log.error("处理待推送告警消息失败, payload={}", payload, e);
            throw new IllegalStateException("处理待推送告警消息失败", e);
        }
    }

    private Map<String, Object> buildSummaryData(AlertPushRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put("personId", request.getPersonId());
        data.put("personName", request.getPersonName());
        data.put("idcard", request.getIdcard());
        data.put("department", request.getDepartment());
        data.put("position", request.getPosition());
        data.put("alertType", request.getAlertType());
        data.put("alertLevel", request.getAlertLevel());
        data.put("alertScore", request.getAlertScore());
        data.put("alertTitle", request.getAlertTitle());
        data.put("alertContent", request.getAlertContent());
        data.put("analysisResult", request.getAnalysisResult());
        data.put("suggestions", request.getSuggestions());
        data.put("alertTime", request.getAlertTime());
        data.put("ext", request.getExt());
        return data;
    }
}
