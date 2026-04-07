package com.tik.aipushcommon.feign.warning;

import com.tik.aipushcommon.feign.ApiServiceNames;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        contextId = "monitorFeignClient",
        name = ApiServiceNames.WARNING_SERVICE,
        path = "/api/monitor"
)
public interface MonitorFeignClient {
    // MonitorController contracts can be added here on demand.
}
