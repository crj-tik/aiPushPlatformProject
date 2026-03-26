package com.tik.aipushpushservice.controller;
import com.tik.aipushpushservice.bean.MessageLog;
import com.tik.aipushpushservice.service.MessagePushService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
public class MessagePushController {

    private final MessagePushService messagePushService;

    /**
     * 同步推送消息
     */
    @PostMapping("/sync")
    public Result<String> pushSync(@RequestBody PushRequest request) {
        String traceId = messagePushService.pushMessage(
                request.getData(),
                request.getPlatform(),
                request.getType()
        );
        return Result.success(traceId);
    }

    /**
     * 异步推送消息
     */
    @PostMapping("/async")
    public Result<String> pushAsync(@RequestBody PushRequest request) {
        messagePushService.asyncPushMessage(
                request.getData(),
                request.getPlatform(),
                request.getType()
        );
        return Result.success("已加入推送队列");
    }

    /**
     * 查询推送结果
     */
    @GetMapping("/result/{traceId}")
    public Result<MessageLog> queryResult(@PathVariable String traceId) {
        MessageLog log = messagePushService.queryResult(traceId);
        return Result.success(log);
    }

    /**
     * 重试失败的消息
     */
    @PostMapping("/retry")
    public Result<Integer> retryFailed() {
        int count = messagePushService.retryFailedMessages();
        return Result.success(count);
    }

    /**
     * 推送统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        Map<String, Object> stats = messagePushService.pushStatistics(startTime, endTime);
        return Result.success(stats);
    }

    @Data
    public static class PushRequest {
        private String data;
        private String platform;
        private String type;
    }

    @Data
    public static class Result<T> {
        private Integer code;
        private String message;
        private T data;

        public static <T> Result<T> success(T data) {
            Result<T> result = new Result<>();
            result.setCode(200);
            result.setMessage("success");
            result.setData(data);
            return result;
        }

        public static <T> Result<T> error(String message) {
            Result<T> result = new Result<>();
            result.setCode(500);
            result.setMessage(message);
            return result;
        }
    }
}
