package com.tik.aipushpushservice.mapper;

import com.tik.aipushpushservice.bean.MessageLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface MessageLogMapper {

    /**
     * 插入消息日志
     */
    int insert(MessageLog messageLog);

    /**
     * 更新推送状态
     */
    int updatePushStatus(@Param("id") Long id,
                         @Param("pushStatus") Integer pushStatus,
                         @Param("errorMessage") String errorMessage);

    /**
     * 根据追踪ID查询
     */
    MessageLog selectByTraceId(@Param("traceId") String traceId);

    /**
     * 查询失败的日志
     */
    List<MessageLog> selectFailedLogs(@Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 统计推送情况
     */
    Map<String, Object> statistics(@Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime);
}
