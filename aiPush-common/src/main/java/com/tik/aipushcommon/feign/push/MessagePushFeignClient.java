package com.tik.aipushcommon.feign.push;

import com.tik.aipushcommon.feign.ApiServiceNames;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        contextId = "messagePushFeignClient",
        name = ApiServiceNames.PUSH_SERVICE,
        path = "/api/push"
)
public interface MessagePushFeignClient {
    // MessagePushController contracts can be added here on demand.
}
