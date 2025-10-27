package com.ly.cookbook.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * RabbitMQ 消息实体类
 *
 * @author admin
 * @date 2025-10-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CookbookMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID（用于幂等性校验）
     */
    private String messageId;

    /**
     * 消息类型（如：user.register, chat.log, token.update 等）
     */
    private String messageType;

    /**
     * 业务数据（JSON 格式）
     */
    private Object data;

    /**
     * 用户ID（可选，用于标识消息发起者）
     */
    private Long userId;

    /**
     * 消息创建时间
     */
    private LocalDateTime createTime;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 创建消息的工厂方法
     */
    public static CookbookMessage create(String messageType, Object data) {
        return CookbookMessage.builder()
                .messageId(java.util.UUID.randomUUID().toString())
                .messageType(messageType)
                .data(data)
                .createTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建带用户ID的消息
     */
    public static CookbookMessage create(String messageType, Object data, Long userId) {
        return CookbookMessage.builder()
                .messageId(java.util.UUID.randomUUID().toString())
                .messageType(messageType)
                .data(data)
                .userId(userId)
                .createTime(LocalDateTime.now())
                .build();
    }
}

