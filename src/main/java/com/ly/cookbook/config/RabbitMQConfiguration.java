package com.ly.cookbook.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 * 配置交换机、队列、绑定关系以及消息转换器
 *
 * @author admin
 * @date 2025-10-20
 */
@Slf4j
@Configuration
public class RabbitMQConfiguration {

    /**
     * 交换机名称
     */
    public static final String COOKBOOK_EXCHANGE = "cookbook";

    /**
     * 队列名称
     */
    public static final String COOKBOOK_QUEUE = "cookbook";

    /**
     * 路由键
     */
    public static final String COOKBOOK_ROUTING_KEY = "cookbook.#";

    /**
     * 声明交换机（Topic 类型，支持模糊匹配）
     */
    @Bean
    public TopicExchange cookbookExchange() {
        return ExchangeBuilder
                .topicExchange(COOKBOOK_EXCHANGE)
                .durable(true)  // 持久化
                .build();
    }

    /**
     * 声明队列
     */
    @Bean
    public Queue cookbookQueue() {
        return QueueBuilder
                .durable(COOKBOOK_QUEUE)  // 持久化
                .build();
    }

    /**
     * 绑定队列到交换机
     */
    @Bean
    public Binding cookbookBinding(Queue cookbookQueue, TopicExchange cookbookExchange) {
        return BindingBuilder
                .bind(cookbookQueue)
                .to(cookbookExchange)
                .with(COOKBOOK_ROUTING_KEY);
    }

    /**
     * 消息转换器（JSON 格式）
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置 RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, 
                                          MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);

        // 消息发送到交换机的确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息发送到交换机成功: {}", correlationData);
            } else {
                log.error("消息发送到交换机失败: {}, 原因: {}", correlationData, cause);
            }
        });

        // 消息从交换机路由到队列失败的回调
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("消息路由到队列失败, 消息: {}, 交换机: {}, 路由键: {}, 原因: {}",
                    returned.getMessage(),
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getReplyText());
        });

        return rabbitTemplate;
    }
}

