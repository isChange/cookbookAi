package com.ly.cookbook.app;

import com.ly.cookbook.advisor.ChatLogAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/10/2 16:22
 * @email liuyia2022@163.com
 */
@Slf4j
@Service
public class CookBookApp {

    private final ChatClient chatClient;
    private final ChatClient chatClientWithMemory;
    private final ChatMemory chatMemory;
    private final String SYSTEM_PROMPT;

    @Autowired
    public CookBookApp(ChatModel dashScopeChatModel, ChatMemory pgChatMemory, Advisor chatTokenHandlerAdvisor,
                       ChatLogAdvisor chatLogAdvisor, Advisor ragCloudAdvisor, ToolCallback[] allTools,
                       @Value("classpath:prompt/cookbook.txt") Resource promptResource) throws IOException {
        // 读取提示词文件
        this.SYSTEM_PROMPT = promptResource.getContentAsString(StandardCharsets.UTF_8);
        log.info("System prompt loaded successfully, length: {} characters", SYSTEM_PROMPT.length());
        // 普通ChatClient（无记忆）
        this.chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(chatLogAdvisor)
                .defaultAdvisors(ragCloudAdvisor)
                .defaultAdvisors(chatTokenHandlerAdvisor)
                .defaultToolCallbacks(allTools)
                .build();
        // 带记忆功能的ChatClient
        this.chatMemory = pgChatMemory;
        // 带记忆功能的ChatClient

        this.chatClientWithMemory = ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(chatLogAdvisor)
                .defaultAdvisors(ragCloudAdvisor)
                .defaultAdvisors(chatTokenHandlerAdvisor)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultToolCallbacks(allTools)
                .build();
        log.info("ChatClient initialized successfully with DashScope");
        log.info("ChatClient with Memory initialized successfully");
    }

    /**
     * 简单的聊天方法示例（无记忆）
     * @param userMessage 用户消息
     * @return AI回复内容流
     */
    public Flux<String> chatByStream(String userMessage) {
        log.info("User message (no memory)");
        Flux<String> response = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(userMessage)
                .stream()
                .content();
        return response;
    }

    /**
     * 简单的聊天方法（流式，带 ChatResponse）
     * 用于需要获取 Token 统计信息的场景
     * 
     * @param userMessage 用户消息
     * @return ChatResponse 流（包含内容和 metadata）
     */
    public Flux<org.springframework.ai.chat.model.ChatResponse> chatByStreamWithResponse(String userMessage) {
        log.info("User message (no memory, with response metadata)");
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(userMessage)
                .stream()
                .chatResponse();
    }
    public String chat(String userMessage) {
        log.info("User message (no memory)");
        String response = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(userMessage)
                .call()
                .content();
        return response;
    }

    /**
     * 带记忆的聊天方法
     * @param conversationId 会话ID（用于区分不同的对话）
     * @param userMessage 用户消息
     * @return AI回复
     */
    public String chatWithMemory(String conversationId, String userMessage) {
        log.info("User message with memory");
        String response = chatClientWithMemory.prompt()
                .system(SYSTEM_PROMPT)
                .user(userMessage)
                .advisors(a -> a.param("chat_memory_conversation_id", conversationId))
                .call()
                .content();
        return response;
    }

    public Flux<String> chatWithMemoryByStream(String conversationId, String userMessage) {
        log.info("User message with memory");
        return chatClientWithMemory.prompt()
                .system(SYSTEM_PROMPT)
                .user(userMessage)
                .advisors(a -> a.param("chat_memory_conversation_id", conversationId))
                .stream()
                .content();
    }

    /**
     * 带记忆的聊天方法（流式，带 ChatResponse）
     * 用于需要获取 Token 统计信息的场景
     * 
     * @param conversationId 会话ID
     * @param userMessage 用户消息
     * @return ChatResponse 流（包含内容和 metadata）
     */
    public Flux<org.springframework.ai.chat.model.ChatResponse> chatWithMemoryByStreamWithResponse(
            String conversationId, String userMessage) {
        log.info("User message with memory (with response metadata)");
        return chatClientWithMemory.prompt()
                .system(SYSTEM_PROMPT)
                .user(userMessage)
                .advisors(a -> a.param("chat_memory_conversation_id", conversationId))
                .stream()
                .chatResponse();
    }

    /**
     * 清除特定会话的记忆
     * @param conversationId 会话ID
     */
    public void clearMemory(String conversationId) {
        chatMemory.clear(conversationId);
        log.info("Cleared memory for conversation: {}", conversationId);
    }

}
