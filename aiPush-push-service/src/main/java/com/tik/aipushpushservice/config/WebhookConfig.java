package com.tik.aipushpushservice.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "webhook")
public class WebhookConfig {
    private String wechat;
    private String feishu;
    private String dingtalk;
    private RetryConfig retry = new RetryConfig();

    @Data
    public static class RetryConfig {
        private int maxAttempts = 3;
        private long initialInterval = 1000;
        private double multiplier = 2.0;
        private long maxInterval = 10000;
    }
}
