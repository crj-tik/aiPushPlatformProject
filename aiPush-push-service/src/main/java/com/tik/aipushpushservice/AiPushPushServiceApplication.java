package com.tik.aipushpushservice;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.tik.aipushcommon.feign")
@EnableKafka
@EnableAsync
@EnableScheduling
@MapperScan("com.tik.aipushpushservice.mapper")
public class AiPushPushServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiPushPushServiceApplication.class, args);
    }
}
