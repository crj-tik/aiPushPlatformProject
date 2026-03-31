package com.tik.aipushpushservice.service.impl;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class OrderIngestionService {

    @Autowired
    private DataIngestionService ingestionService;

    @PostConstruct  // 或在需要时手动调用
    public void ingestOrders() {
        ingestionService.ingestDatabaseTable(
                "orders",                    // 表名
                "id",                         // 主键
                Arrays.asList("order_no", "customer_name", "product_name", "total_amount"), // 内容列
                Arrays.asList("order_status", "created_at", "region") // 元数据列
        );
    }
}
