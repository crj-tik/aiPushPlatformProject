package com.tik.aipushcommon.feign.push;

import com.tik.aipushcommon.feign.ApiServiceNames;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        contextId = "knowledgeIngestionFeignClient",
        name = ApiServiceNames.PUSH_SERVICE,
        path = "/api/knowledge"
)
public interface KnowledgeIngestionFeignClient {
    // KnowledgeIngestionController contracts can be added here on demand.
}
