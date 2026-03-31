package com.tik.aipushwarningservice.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient fastChatClient(
            ChatClient.Builder builder,
            @Value("${warning.ai.fast.model:qwen-turbo}") String model,
            @Value("${warning.ai.fast.temperature:0.3}") Double temperature) {
        return builder
                .defaultOptions(DashScopeChatOptions.builder()
                        .withModel(model)
                        .withTemperature(temperature)
                        .build())
                .build();
    }

    @Bean
    public ChatClient accurateChatClient(
            ChatClient.Builder builder,
            @Value("${warning.ai.accurate.model:qwen-max}") String model,
            @Value("${warning.ai.accurate.temperature:0.7}") Double temperature) {
        return builder
                .defaultOptions(DashScopeChatOptions.builder()
                        .withModel(model)
                        .withTemperature(temperature)
                        .build())
                .build();
    }
}
