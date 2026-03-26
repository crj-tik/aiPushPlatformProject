package com.tik.aipushgatewayservice.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gateway 控制器测试类
 * 使用 WebTestClient 测试响应式端点
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    /**
     * 测试 hello 端点
     */
    @Test
    void testHelloEndpoint() {
        webTestClient.get()
                .uri("/test/hello")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Hello from Gateway Service")
                .jsonPath("$.status").isEqualTo("success");
    }

    /**
     * 测试 health 端点
     */
    @Test
    void testHealthEndpoint() {
        webTestClient.get()
                .uri("/test/health")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP")
                .jsonPath("$.service").isEqualTo("gateway-service");
    }

    /**
     * 测试404处理
     */
    @Test
    void testNotFoundEndpoint() {
        webTestClient.get()
                .uri("/test/not-found")
                .exchange()
                .expectStatus().isNotFound();
    }
}
