package com.ly.cookbook.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.ly.cookbook.enums.AgentStateEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/10/3 21:11
 * @email liuyia2022@163.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class ToolCallAgent extends ReActAgent{
    private final ToolCallback[] availableTools;
    private final ToolCallingManager toolCallingManager;
    private final ChatOptions chatOptions;
    private ChatResponse toolCallChatResponse;
    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        // 移除 toolChoice 限制，让 LLM 能返回 toolCalls（但在 think 中不自动执行）
        this.chatOptions = DashScopeChatOptions.builder()
                .withInternalToolExecutionEnabled(false)
                .build();
    }
    @Override
    public boolean think() {
        if (StringUtils.isNotBlank(getNextStepPrompt())){
            getChatMessage().add(new UserMessage(getNextStepPrompt()));
        }
        List<AssistantMessage.ToolCall> toolCalls = null;
        try {
            // 关键修改：使用 Prompt 传递 chatOptions，这样 LLM 会返回 toolCalls
            // 但不会自动执行工具，工具的实际执行将在 act() 方法中通过 ToolCallingManager 手动进行
            Prompt prompt = new Prompt(getChatMessage(), chatOptions);
            
            //调用 LLM 思考
            // 注意：这里使用 .toolCallbacks() 是为了让 LLM 知道有哪些工具可用
            // 但由于我们不调用 .stream() 或其他自动执行的方法，工具不会被自动执行
            ChatResponse chatResponse = getChatClient()
                    .prompt(prompt)
                    .system(getSystemPrompt())
                    .toolCallbacks(availableTools)  // 只是注册工具定义，让 LLM 知道可用的工具
                    .advisors(a -> a.param("chat_memory_conversation_id", getCurrentConversationId()))
                    .stream()
                    .chatResponse()
                    .doOnNext(response -> {
                        String text = response.getResult().getOutput().getText();
                        if (!StringUtils.isBlank(text)){
                            SseEmitter sseEmitter = getSseEmitter();
                            if (sseEmitter != null){
                                try {
                                    // 使用自定义事件类型 "thinking" 发送思考过程
                                    sseEmitter.send(SseEmitter.event()
                                            .name("thinking")
                                            .data(text));
                                } catch (IOException e) {
                                    log.warn("SSE信息发送失败");
                                }
                            }
                        }
                    })
                    .blockLast();
            //设置思考响应
            setToolCallChatResponse(chatResponse);
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            //获取 LLM 决定调用的工具（此时还未执行）
            toolCalls = assistantMessage.getToolCalls();
            if (!CollectionUtil.isEmpty(toolCalls)) {
                String toolsDesc = toolCalls.stream().map(toolCall -> String.format("工具名称：%s, 工具参数：%s", toolCall.name(), toolCall.arguments()))
                        .collect(Collectors.joining("\n"));
                log.info("LLM 决定调用的工具（尚未执行）：{}", toolsDesc);
            } else {
                log.info("LLM 决定不调用任何工具");
            }
            return !CollectionUtil.isEmpty(toolCalls);
        } catch (Exception e) {
            log.error(getName() + "的思考过程遇到了问题: " + e.getMessage());
            getChatMessage().add(new AssistantMessage("思考过程遇到了问题: " + e.getMessage()));
            return false;
        }

    }

    @Override
    public String act() {
        if (!this.toolCallChatResponse.hasToolCalls()){
            return "没有调用工具";
        }
        //获取思考响应
        ChatResponse chatResponse = getToolCallChatResponse();
        Integer totalTokens = chatResponse.getMetadata().getUsage().getTotalTokens();
        setUsedToken(getUsedToken() + Long.valueOf(totalTokens));
        String text = chatResponse.getResult().getOutput().getText();
        //调用工具
        Prompt prompt = new Prompt(getChatMessage(), chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, chatResponse);
        // 处理工具执行结果
        List<Message> messages = toolExecutionResult.conversationHistory();
        setChatMessage(messages);
        ToolResponseMessage result = (ToolResponseMessage) CollUtil.getLast(messages);
        String content = result.getResponses().stream()
                .map(toolResponse -> String.format("工具名称：%s", toolResponse.name()))
                .collect(Collectors.joining("\n"));
        log.info("工具执行信息：{}", content);
        //查看是否调用终止工具
        boolean doTerminate = result.getResponses().stream().anyMatch(toolResponse -> toolResponse.name().equals("doTerminate"));
        if (doTerminate){
            setState(AgentStateEnum.FINISHED);
        }
        return content;
    }
}
