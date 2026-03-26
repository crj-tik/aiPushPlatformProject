package com.tik.aipusharchiveservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.tik.aipusharchiveservice.mapper")
public class AiPushArchiveServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiPushArchiveServiceApplication.class, args);
    }

}
