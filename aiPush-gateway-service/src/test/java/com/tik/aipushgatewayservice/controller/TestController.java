package com.tik.aipushgatewayservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * 测试端点
     */
    @GetMapping("/hello")
    public Mono<Map<String, String>> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello from Gateway Service");
        response.put("status", "success");
        return Mono.just(response);
    }

    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public Mono<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "gateway-service");
        return Mono.just(health);
    }
}
