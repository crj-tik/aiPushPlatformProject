package com.tik.aipushgatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AiPushGatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiPushGatewayServiceApplication.class, args);
    }

}
