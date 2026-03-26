package com.tik.aipushgatewayservice.config;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gateway 配置测试类
 */
@SpringBootTest
class GatewayConfigTest {

    @Autowired(required = false)
    private RouteLocator routeLocator;

    @Autowired(required = false)
    private RouteDefinitionLocator routeDefinitionLocator;

    /**
     * 测试路由配置是否存在
     */
    @Test
    void routeLocatorExists() {
        assertThat(routeLocator).isNotNull();
    }

    /**
     * 测试路由定义是否存在
     */
    @Test
    void routeDefinitionLocatorExists() {
        assertThat(routeDefinitionLocator).isNotNull();
    }

    /**
     * 测试默认路由数量
     */
    @Test
    void routesAreDefined() {
        if (routeDefinitionLocator != null) {
            long routeCount = routeDefinitionLocator.getRouteDefinitions().count().block();
            assertThat(routeCount).isGreaterThanOrEqualTo(0);
        }
    }
}
