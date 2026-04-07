package com.tik.aipushcommon.feign.push;

import com.tik.aipushcommon.feign.ApiServiceNames;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        contextId = "webhookFeignClient",
        name = ApiServiceNames.PUSH_SERVICE,
        path = "/webhook"
)
public interface WebhookFeignClient {
    // WebhookController contracts can be added here on demand.
}
