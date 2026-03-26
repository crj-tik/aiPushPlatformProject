package com.tik.aipushgatewayservice;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gateway 服务主测试类
 * 测试应用上下文加载和基础配置
 */
@SpringBootTest
class AiPushGatewayServiceApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private DiscoveryClient discoveryClient;

    @Autowired
    private Environment environment;

    /**
     * 测试应用上下文是否成功加载
     */
    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    /**
     * 测试主类是否存在
     */
    @Test
    void mainMethodExists() {
        AiPushGatewayServiceApplication.main(new String[]{});
        assertThat(true).isTrue();
    }

    /**
     * 测试服务名称配置
     */
    @Test
    void applicationNameIsConfigured() {
        String appName = environment.getProperty("spring.application.name");
        assertThat(appName).isEqualTo("gateway-service");
    }

    /**
     * 测试服务发现客户端是否启用
     */
    @Test
    void discoveryClientIsEnabled() {
        assertThat(discoveryClient).isNotNull();
    }

    /**
     * 测试端口配置
     */
    @Test
    void serverPortIsConfigured() {
        String port = environment.getProperty("server.port");
        assertThat(port).isNotNull();
    }

    /**
     * 测试Nacos配置
     */
    @Test
    void nacosConfigIsLoaded() {
        String serverAddr = environment.getProperty("spring.cloud.nacos.discovery.server-addr");
        assertThat(serverAddr).isNotNull();
    }
}
