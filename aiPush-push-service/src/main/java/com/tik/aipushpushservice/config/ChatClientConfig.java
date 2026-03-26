package com.tik.aipushpushservice.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient qwenChatClient(
            ChatClient.Builder builder,
            @Value("${spring.ai.dashscope.chat.options.model:qwen-plus}") String model,
            @Value("${spring.ai.dashscope.chat.options.temperature:0.7}") Double temperature,
            @Value("${spring.ai.dashscope.chat.options.max-tokens:1000}") Integer maxTokens) {
        return builder.defaultOptions(DashScopeChatOptions.builder()
                .withModel(model)
                .withTemperature(temperature)
                .withMaxToken(maxTokens)
                .build()).build();
    }
}
