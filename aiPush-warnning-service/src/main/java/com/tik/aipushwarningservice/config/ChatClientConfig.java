package com.tik.aipushwarningservice.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {
    @Bean
    public ChatClient fastChatClient(ChatClient.Builder builder) {
        return builder
                .defaultOptions(DashScopeChatOptions.builder()
                        .withModel("qwen-turbo")
                        .withTemperature(0.3)
                        .build())
                .build();
    }

    @Bean
    public ChatClient accurateChatClient(ChatClient.Builder builder) {
        return builder
                .defaultOptions(DashScopeChatOptions.builder()
                        .withModel("qwen-max")
                        .withTemperature(0.7)
                        .build())
                .build();
    }
}