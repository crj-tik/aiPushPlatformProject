package com.tik.aipushcommon.feign.push;

import com.tik.aipushcommon.feign.ApiServiceNames;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        contextId = "ingestionAdminFeignClient",
        name = ApiServiceNames.PUSH_SERVICE,
        path = "/api/admin/ingest"
)
public interface IngestionAdminFeignClient {
    // IngestionAdminController contracts can be added here on demand.
}
