package com.ly.cookbook.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ly.cookbook.common.model.Result;
import com.ly.cookbook.mq.dto.CookbookMessage;
import com.ly.cookbook.mq.producer.MessageSender;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 测试接口
 *
 * @author admin
 * @date 2025-10-20
 */
@Slf4j
@RestController
@RequestMapping("/mq/test")
@Tag(name = "RabbitMQ 测试接口", description = "用于测试 RabbitMQ 消息发送和消费")
public class RabbitMQTestController {

    @Resource
    private MessageSender messageSender;

    @Operation(summary = "发送简单测试消息", description = "发送一条简单的测试消息到 RabbitMQ")
    @PostMapping("/send-simple")
    public Result<String> sendSimpleMessage(
            @RequestParam @Parameter(description = "消息内容") String content) {
        
        messageSender.sendSimple("test.message", content);
        
        return Result.success("消息发送成功");
    }

    @Operation(summary = "发送带用户ID的消息", description = "发送带用户ID的测试消息")
    @PostMapping("/send-with-user")
    public Result<String> sendMessageWithUser(
            @RequestParam @Parameter(description = "消息内容") String content) {
        
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            messageSender.sendWithUserId("test.message", content, userId);
            return Result.success("消息发送成功，用户ID: " + userId);
        } catch (Exception e) {
            messageSender.sendSimple("test.message", content);
            return Result.success("消息发送成功（未登录）");
        }
    }

    @Operation(summary = "发送复杂对象消息", description = "发送包含复杂对象的消息")
    @PostMapping("/send-object")
    public Result<String> sendObjectMessage() {
        
        Map<String, Object> data = new HashMap<>();
        data.put("username", "testUser");
        data.put("email", "test@example.com");
        data.put("timestamp", System.currentTimeMillis());
        
        messageSender.sendSimple("user.register", data);
        
        return Result.success("用户注册消息发送成功");
    }

    @Operation(summary = "发送聊天日志消息", description = "发送聊天日志消息示例")
    @PostMapping("/send-chat-log")
    public Result<String> sendChatLogMessage(
            @RequestParam @Parameter(description = "用户问题") String question,
            @RequestParam @Parameter(description = "AI回答") String answer) {
        
        Map<String, Object> chatLog = new HashMap<>();
        chatLog.put("question", question);
        chatLog.put("answer", answer);
        chatLog.put("tokens", 1000);
        chatLog.put("timestamp", System.currentTimeMillis());
        
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            messageSender.sendWithUserId("chat.log", chatLog, userId);
            return Result.success("聊天日志消息发送成功");
        } catch (Exception e) {
            messageSender.sendSimple("chat.log", chatLog);
            return Result.success("聊天日志消息发送成功（未登录）");
        }
    }

    @Operation(summary = "发送 Token 更新消息", description = "发送用户 Token 使用量更新消息")
    @PostMapping("/send-token-update")
    public Result<String> sendTokenUpdateMessage(
            @RequestParam @Parameter(description = "消耗的 Token 数量") Long tokens) {
        
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            
            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put("consumedTokens", tokens);
            tokenData.put("timestamp", System.currentTimeMillis());
            
            messageSender.sendWithUserId("token.update", tokenData, userId);
            
            return Result.success("Token 更新消息发送成功");
        } catch (Exception e) {
            return Result.fail("请先登录");
        }
    }

    @Operation(summary = "发送自定义消息", description = "发送自定义类型的消息")
    @PostMapping("/send-custom")
    public Result<String> sendCustomMessage(
            @RequestParam @Parameter(description = "消息类型") String messageType,
            @RequestParam @Parameter(description = "消息内容") String content) {
        
        CookbookMessage message = CookbookMessage.builder()
                .messageId(java.util.UUID.randomUUID().toString())
                .messageType(messageType)
                .data(content)
                .createTime(java.time.LocalDateTime.now())
                .remark("自定义消息")
                .build();
        
        messageSender.send(message);
        
        return Result.success("自定义消息发送成功，消息ID: " + message.getMessageId());
    }

    @Operation(summary = "批量发送消息", description = "批量发送测试消息")
    @PostMapping("/send-batch")
    public Result<String> sendBatchMessages(
            @RequestParam(defaultValue = "10") @Parameter(description = "消息数量") int count) {
        
        for (int i = 0; i < count; i++) {
            String content = "批量测试消息 #" + (i + 1);
            messageSender.sendSimple("test.message", content);
        }
        
        return Result.success("批量发送完成，共发送 " + count + " 条消息");
    }
}

