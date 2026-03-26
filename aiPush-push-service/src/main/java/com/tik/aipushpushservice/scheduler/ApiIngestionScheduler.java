package com.tik.aipushpushservice.scheduler;

import com.tik.aipushpushservice.service.impl.DataIngestionService;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ApiIngestionScheduler {

    @Autowired
    private DataIngestionService ingestionService;

    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void syncProducts() {
        ingestionService.ingestApiData(
                "http://product-service/api/products",
                response -> {
                    // 将接口返回的每个产品转为 Document
                    String content = String.format("产品名称：%s，描述：%s，价格：%s，库存：%s",
                            response.get("name"),
                            response.get("description"),
                            response.get("price"),
                            response.get("stock")
                    );

                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("source_type", "api");
                    metadata.put("source_api", "/api/products");
                    metadata.put("product_id", response.get("id"));
                    metadata.put("category", response.get("category"));

                    return new Document(
                            "product_" + response.get("id"),
                            content,
                            metadata
                    );
                }
        );
    }
}
