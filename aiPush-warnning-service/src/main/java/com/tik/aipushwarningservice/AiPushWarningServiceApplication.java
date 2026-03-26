package com.tik.aipushwarningservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.tik.aipushwarningservice.mapper")
public class AiPushWarningServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiPushWarningServiceApplication.class, args);
    }

}
