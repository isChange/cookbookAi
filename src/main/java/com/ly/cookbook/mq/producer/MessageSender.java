package com.ly.cookbook.mq.producer;

import com.ly.cookbook.config.RabbitMQConfiguration;
import com.ly.cookbook.mq.dto.CookbookMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ 消息发送工具类
 *
 * @author admin
 * @date 2025-10-20
 */
@Slf4j
@Component
public class MessageSender {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到 cookbook 交换机
     *
     * @param message 消息对象
     */
    public void send(CookbookMessage message) {
        send(message, RabbitMQConfiguration.COOKBOOK_ROUTING_KEY);
    }

    /**
     * 发送消息到 cookbook 交换机（指定路由键）
     *
     * @param message    消息对象
     * @param routingKey 路由键
     */
    public void send(CookbookMessage message, String routingKey) {
        try {
            log.info("准备发送消息: messageId={}, type={}, routingKey={}",
                    message.getMessageId(), message.getMessageType(), routingKey);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfiguration.COOKBOOK_EXCHANGE,
                    routingKey,
                    message
            );

            log.info("消息发送成功: messageId={}", message.getMessageId());
        } catch (Exception e) {
            log.error("消息发送失败: messageId={}, error={}",
                    message.getMessageId(), e.getMessage(), e);
            throw new RuntimeException("消息发送失败", e);
        }
    }

    /**
     * 发送简单消息（快捷方法）
     *
     * @param messageType 消息类型
     * @param data        业务数据
     */
    public void sendSimple(String messageType, Object data) {
        CookbookMessage message = CookbookMessage.create(messageType, data);
        send(message);
    }

    /**
     * 发送带用户ID的消息（快捷方法）
     *
     * @param messageType 消息类型
     * @param data        业务数据
     * @param userId      用户ID
     */
    public void sendWithUserId(String messageType, Object data, Long userId) {
        CookbookMessage message = CookbookMessage.create(messageType, data, userId);
        send(message);
    }

    /**
     * 发送延迟消息（使用 TTL 实现，需要配置死信队列）
     * 
     * 注意：如果需要使用真正的延迟队列，需要：
     * 1. 安装 RabbitMQ 延迟插件：rabbitmq_delayed_message_exchange
     * 2. 修改交换机类型为 x-delayed-message
     *
     * @param message 消息对象
     * @param delayMs 延迟时间（毫秒）
     */
    public void sendDelayed(CookbookMessage message, long delayMs) {
        try {
            log.info("准备发送延迟消息: messageId={}, type={}, delay={}ms",
                    message.getMessageId(), message.getMessageType(), delayMs);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfiguration.COOKBOOK_EXCHANGE,
                    RabbitMQConfiguration.COOKBOOK_ROUTING_KEY,
                    message,
                    msg -> {
                        // 设置消息过期时间（TTL）
                        msg.getMessageProperties().setExpiration(String.valueOf(delayMs));
                        return msg;
                    }
            );

            log.info("延迟消息发送成功: messageId={}", message.getMessageId());
        } catch (Exception e) {
            log.error("延迟消息发送失败: messageId={}, error={}",
                    message.getMessageId(), e.getMessage(), e);
            throw new RuntimeException("延迟消息发送失败", e);
        }
    }
}

