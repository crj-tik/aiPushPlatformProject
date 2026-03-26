package com.tik.aipushpushservice.config;


import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NacosConfig {

    @Value("${webhook.wechat:}")
    private String wechatWebhook;

    @Value("${webhook.feishu:}")
    private String feishuWebhook;

    @Value("${webhook.dingtalk:}")
    private String dingtalkWebhook;

    @Bean
    @RefreshScope
    public WebhookConfig webhookConfig() {
        WebhookConfig config = new WebhookConfig();
        config.setWechat(wechatWebhook);
        config.setFeishu(feishuWebhook);
        config.setDingtalk(dingtalkWebhook);
        return config;
    }
}