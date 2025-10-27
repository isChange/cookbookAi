package com.ly.cookbook.mq.consumer;

import com.ly.cookbook.config.RabbitMQConfiguration;
import com.ly.cookbook.mq.dto.CookbookMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * RabbitMQ 消息消费者
 *
 * @author admin
 * @date 2025-10-20
 */
@Slf4j
@Component
public class MessageConsumer {

    /**
     * 监听 cookbook 队列，消费消息
     *
     * @param cookbookMessage 消息对象
     * @param message         原始消息（用于手动确认）
     * @param channel         通道（用于手动确认）
     */
    @RabbitListener(queues = RabbitMQConfiguration.COOKBOOK_QUEUE)
    public void consumeMessage(CookbookMessage cookbookMessage, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            log.info("收到消息: messageId={}, type={}, userId={}, data={}",
                    cookbookMessage.getMessageId(),
                    cookbookMessage.getMessageType(),
                    cookbookMessage.getUserId(),
                    cookbookMessage.getData());

            // 根据消息类型分发处理
            handleMessageByType(cookbookMessage);

            // 手动确认消息（ACK）
            channel.basicAck(deliveryTag, false);
            log.info("消息处理成功并已确认: messageId={}", cookbookMessage.getMessageId());

        } catch (Exception e) {
            log.error("消息处理失败: messageId={}, error={}",
                    cookbookMessage.getMessageId(), e.getMessage(), e);

            try {
                // 消息处理失败，拒绝消息并重新入队（可根据业务需求调整）
                // 第三个参数 true 表示重新入队，false 表示丢弃或进入死信队列
                channel.basicNack(deliveryTag, false, true);
                log.warn("消息已重新入队: messageId={}", cookbookMessage.getMessageId());
            } catch (IOException ioException) {
                log.error("消息拒绝失败: messageId={}", cookbookMessage.getMessageId(), ioException);
            }
        }
    }

    /**
     * 根据消息类型分发处理逻辑
     *
     * @param message 消息对象
     */
    private void handleMessageByType(CookbookMessage message) {
        String messageType = message.getMessageType();

        switch (messageType) {
            case "user.register":
                handleUserRegister(message);
                break;

            case "chat.log":
                handleChatLog(message);
                break;

            case "token.update":
                handleTokenUpdate(message);
                break;

            case "test.message":
                handleTestMessage(message);
                break;

            default:
                log.warn("未知的消息类型: {}", messageType);
        }
    }

    /**
     * 处理用户注册消息
     */
    private void handleUserRegister(CookbookMessage message) {
        log.info("处理用户注册消息: {}", message.getData());
        // TODO: 实现用户注册后的业务逻辑
        // 例如：发送欢迎邮件、初始化用户数据等
    }

    /**
     * 处理聊天日志消息
     */
    private void handleChatLog(CookbookMessage message) {
        log.info("处理聊天日志消息: {}", message.getData());
        // TODO: 实现聊天日志记录逻辑
        // 例如：存储到数据库、统计分析等
    }

    /**
     * 处理 Token 更新消息
     */
    private void handleTokenUpdate(CookbookMessage message) {
        log.info("处理 Token 更新消息: userId={}, data={}",
                message.getUserId(), message.getData());
        // TODO: 实现 Token 更新逻辑
        // 例如：更新用户剩余 Token 数量
    }

    /**
     * 处理测试消息
     */
    private void handleTestMessage(CookbookMessage message) {
        log.info("处理测试消息: {}", message.getData());
        // 仅用于测试
    }
}

