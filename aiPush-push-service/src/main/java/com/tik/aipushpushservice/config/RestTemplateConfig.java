package com.tik.aipushpushservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Collections;

@Slf4j
@Configuration
public class RestTemplateConfig {

    @Value("${rest.template.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${rest.template.read-timeout:30000}")
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate() {
        // 1. 创建 HttpClient
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeout))
                .version(HttpClient.Version.HTTP_2)
                .build();

        // 2. 创建请求工厂
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofMillis(readTimeout));

        // 3. 创建 RestTemplate
        RestTemplate restTemplate = new RestTemplate(factory);

        // 4. 添加日志拦截器
        restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));

        return restTemplate;
    }

    /**
     * 日志拦截器 - 记录 API 调用详情
     */
    private static class LoggingInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
            long startTime = System.currentTimeMillis();

            // 记录请求
            log.info("API请求: {} {}", request.getMethod(), request.getURI());

            try {
                ClientHttpResponse response = execution.execute(request, body);
                long duration = System.currentTimeMillis() - startTime;

                // 记录响应
                log.info("API响应: {} 状态码: {}, 耗时: {}ms",
                        request.getURI(), response.getStatusCode(), duration);

                return response;
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - startTime;
                log.error("API调用失败: {}, 耗时: {}ms, 错误: {}",
                        request.getURI(), duration, e.getMessage());
                throw e;
            }
        }
    }
}