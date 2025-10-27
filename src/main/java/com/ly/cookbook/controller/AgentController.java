package com.ly.cookbook.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ly.cookbook.agent.YiCookAgent;
import com.ly.cookbook.common.units.AssertUtil;
import com.ly.cookbook.exception.emun.AgentErrorEnum;
import com.ly.cookbook.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/10/5 19:25
 * @email liuyia2022@163.com
 */
@Slf4j
@RestController
@RequestMapping("/agent")
@Tag(name = "agent智能体接口", description = "AI聊天相关接口")
public class AgentController{
    @Resource
    private ToolCallback[] allTools;
    @Resource
    private ChatModel dashScopeChatModel;
    @Resource
    private ChatMemory pgChatMemory;
    @Resource
    private Advisor chatLogAdvisor;
    @Resource
    private Advisor ragCloudAdvisor;
    @Resource
    private Advisor chatTokenHandlerAdvisor;
    @Resource
    private UserService userService;
    @Operation(summary = "Agent对话", description = "Agent对话")
    @GetMapping(value = "/yicook")
    public String chatWithAgent(
            @RequestParam @Parameter(description = "会话ID") String conversationId,
            @RequestParam @Parameter(description = "用户消息") String message) {
        return new YiCookAgent(allTools, dashScopeChatModel, pgChatMemory,
                chatLogAdvisor, ragCloudAdvisor, chatTokenHandlerAdvisor).runBySync(conversationId, message);
    }

    @Operation(summary = "Agent对话(SSE)", description = "Agent对话(SSE)")
    @GetMapping(value = "/yicook/stream/sse", produces = "text/event-stream")
    public SseEmitter chatWithAgentBySSE(
            @RequestParam @Parameter(description = "会话ID") String conversationId,
            @RequestParam @Parameter(description = "用户消息") String message
    ){
        StpUtil.checkLogin();
        return new YiCookAgent(allTools, dashScopeChatModel, pgChatMemory,
                chatLogAdvisor, ragCloudAdvisor, chatTokenHandlerAdvisor).runByStream(conversationId, message);
    }
}
