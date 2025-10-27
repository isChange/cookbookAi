package com.ly.cookbook.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ly.cookbook.app.CookBookApp;
import com.ly.cookbook.common.model.Result;
import com.ly.cookbook.common.units.AssertUtil;
import com.ly.cookbook.exception.emun.AgentErrorEnum;
import com.ly.cookbook.model.SpringAiChatMemory;
import com.ly.cookbook.service.SpringAiChatMemoryService;
import com.ly.cookbook.service.TokenStatisticsService;
import com.ly.cookbook.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description 聊天控制器 - 演示记忆对话功能
 * @createDate：2025/10/2 17:30
 * @email liuyia2022@163.com
 */
@Slf4j
@RestController
@RequestMapping("/chat")
@Tag(name = "聊天接口", description = "AI聊天相关接口")
public class ChatController {
    @Resource
    private UserService userService;

    @Resource
    private CookBookApp cookBookApp;

    @Resource
    private SpringAiChatMemoryService springAiChatMemoryService;

    @Resource
    private TokenStatisticsService tokenStatisticsService;

    @Operation(summary = "普通聊天", description = "无记忆的单次对话")
    @GetMapping("/simple")
    public Result<String> simpleChat(@RequestParam @Parameter(description = "用户消息") String message) {
        AssertUtil.isTrue(userService.checkUserToken(StpUtil.getLoginIdAsLong()), AgentErrorEnum.USER_TOKEN_EMPTY);
        try {
            String response = cookBookApp.chat(message);
            return Result.success(response);
        } catch (Exception e) {
            log.error("Simple chat error", e);
            return Result.fail("聊天失败: " + e.getMessage());
        }
    }

    @Operation(summary = "普通聊天(SSE)", description = "无记忆的单次对话(SSE)")
    @GetMapping(value = "/simple/stream/sse")
    public SseEmitter simpleChatByStream(@RequestParam @Parameter(description = "用户消息") String message) {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        SseEmitter sseEmitter = new SseEmitter(180000L);
        // 使用带 ChatResponse 的方法，以便获取 Token 统计信息
        AtomicReference<Usage> usageRef = new AtomicReference<>();
        
        cookBookApp.chatByStreamWithResponse(message)
                .subscribe(
                        chatResponse -> {
                            try {
                                // 发送内容到前端
                                String content = chatResponse.getResult().getOutput().getText();
                                if (content != null && !content.isEmpty()) {
                                    sseEmitter.send(content);
                                }
                                
                                // 保存最新的 Usage 信息
                                if (chatResponse.getMetadata() != null && chatResponse.getMetadata().getUsage() != null) {
                                    usageRef.set(chatResponse.getMetadata().getUsage());
                                }
                            } catch (IOException e) {
                                sseEmitter.completeWithError(e);
                            }
                        },
                        sseEmitter::completeWithError,
                        () -> {
                            // 流完成时统计 Token
                            Usage usage = usageRef.get();
                            if (usage != null) {
                                tokenStatisticsService.updateUserToken(userId, usage);
                            }
                            sseEmitter.complete();
                        }
                );
        return sseEmitter;
    }

    @Operation(summary = "记忆对话", description = "带上下文记忆的对话，通过conversationId区分不同会话")
    @GetMapping("/memory")
    public Result<String> chatWithMemory(
            @RequestParam @Parameter(description = "会话ID") String conversationId,
            @RequestParam @Parameter(description = "用户消息") String message) {
        AssertUtil.isTrue(userService.checkUserToken(StpUtil.getLoginIdAsLong()), AgentErrorEnum.USER_TOKEN_EMPTY);
        try {
            String response = cookBookApp.chatWithMemory(conversationId, message);
            return Result.success(response);
        } catch (Exception e) {
            log.error("Memory chat error", e);
            return Result.fail("聊天失败: " + e.getMessage());
        }
    }

    @Operation(summary = "记忆对话(SSE)", description = "带上下文记忆的对话，通过conversationId区分不同会话(SSE)")
    @GetMapping(value = "/memory/stream/sse")
    public SseEmitter chatWithMemoryBySSE(
            @RequestParam @Parameter(description = "会话ID") String conversationId,
            @RequestParam @Parameter(description = "用户消息") String message) {
        StpUtil.checkLogin();
        SseEmitter sseEmitter = new SseEmitter(180000L);
        Long userId = StpUtil.getLoginIdAsLong();
        // 使用带 ChatResponse 的方法，以便获取 Token 统计信息
        AtomicReference<Usage> usageRef = new AtomicReference<>();
        
        cookBookApp.chatWithMemoryByStreamWithResponse(conversationId, message)
                .subscribe(
                        chatResponse -> {
                            try {
                                // 发送内容到前端
                                String content = chatResponse.getResult().getOutput().getText();
                                if (content != null && !content.isEmpty()) {
                                    sseEmitter.send(content);
                                }
                                
                                // 保存最新的 Usage 信息
                                if (chatResponse.getMetadata() != null && chatResponse.getMetadata().getUsage() != null) {
                                    usageRef.set(chatResponse.getMetadata().getUsage());
                                }
                            } catch (IOException e) {
                                sseEmitter.completeWithError(e);
                            }
                        },
                        sseEmitter::completeWithError,
                        () -> {
                            // 流完成时统计 Token
                            Usage usage = usageRef.get();
                            if (usage != null) {
                                tokenStatisticsService.updateUserToken(userId, usage);
                            }
                            sseEmitter.complete();
                        }
                );
        return sseEmitter;
    }

    @Operation(summary = "清除会话记忆", description = "清除指定会话的所有历史记录")
    @DeleteMapping("/memory/{conversationId}")
    public Result<Flux<String>> clearMemory(
            @PathVariable @Parameter(description = "会话ID") String conversationId) {
        try {
            cookBookApp.clearMemory(conversationId);
            return Result.success();
        } catch (Exception e) {
            log.error("Clear memory error", e);
            return Result.fail("清除记忆失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取会话列表", description = "获取所有会话列表")
    @GetMapping("/memory/list")
    public Result<List<SpringAiChatMemory>> getConversationList() {
        return Result.success(springAiChatMemoryService.list());
    }
}

