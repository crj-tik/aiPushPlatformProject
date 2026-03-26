package com.tik.aipushpushservice.controller;

import com.tik.aipushpushservice.service.impl.DataIngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/admin/ingest")
public class IngestionAdminController {

    @Autowired
    private DataIngestionService ingestionService;

    @PostMapping("/table/{tableName}")
    public String ingestTable(@PathVariable String tableName) {
        // 根据表名选择不同的列配置
        switch (tableName) {
            case "orders":
                ingestionService.ingestDatabaseTable(
                        "orders", "id",
                        Arrays.asList("order_no", "customer_name", "product_details"),
                        Arrays.asList("status", "amount")
                );
                return "订单表同步完成";
            case "users":
                ingestionService.ingestDatabaseTable(
                        "users", "id",
                        Arrays.asList("username", "email", "phone"),
                        Arrays.asList("status", "level")
                );
                return "用户表同步完成";
            default:
                return "未知表名";
        }
    }

    @PostMapping("/api/{apiName}")
    public String ingestApi(@PathVariable String apiName) {
        // 手动触发接口数据同步
        return "接口同步任务已触发";
    }
}
