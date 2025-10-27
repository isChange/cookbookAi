package com.ly.cookbook.agent;

import cn.dev33.satoken.stp.StpUtil;
import com.ly.cookbook.common.units.AssertUtil;
import com.ly.cookbook.common.units.SpringContextUtil;
import com.ly.cookbook.enums.AgentStateEnum;
import com.ly.cookbook.exception.emun.AgentErrorEnum;
import com.ly.cookbook.service.TokenStatisticsService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/10/3 17:08
 * @email liuyia2022@163.com
 */
@Data
@Slf4j
public abstract class BaseAgent {
    private AgentStateEnum state = AgentStateEnum.IDLE;
    private String name;
    private String systemPrompt;
    private String nextStepPrompt;
    private String finalSummaryPrompt;
    private final Integer maxSteps = 10;
    private Integer currentStep = 0;
    private ChatClient chatClient;
    private List<Message> chatMessage = new ArrayList<>();
    private String currentConversationId;
    private ChatMemory pgChatMemory;
    private SseEmitter sseEmitter;
    private Long usedToken = 0L;


    public String run(String conversationId, String userPrompt) {
        AssertUtil.isNotBlank(conversationId, AgentErrorEnum.CONVERSATION_ID_EMPTY);
        AssertUtil.isEquals(AgentStateEnum.IDLE, state, AgentErrorEnum.SYSTEM_BUSY);
        AssertUtil.isNotBlank(userPrompt, AgentErrorEnum.INPUT_EMPTY);
        // 开始执行,变更状态
        state = AgentStateEnum.RUNNING;
        //设置当前会话ID
        currentConversationId = conversationId;

        log.info("Agent 开始执行");

        List<String> result = new ArrayList<>();
        //加载聊天记录
        chatMessage.addAll(pgChatMemory.get(conversationId));
        //加载用户提问
        chatMessage.add(new UserMessage(userPrompt));
        try {
            while (currentStep < maxSteps && state != AgentStateEnum.FINISHED) {
                currentStep++;
                log.info("当前步骤：{}/{}", currentStep, maxSteps);
                String stepResult = step();
                String formatResult = String.format("当前步骤：%s/%s,当前步骤结果：%s", currentStep, maxSteps, stepResult);
                result.add(formatResult);
            }
            if (currentStep >= maxSteps) {
                state = AgentStateEnum.FINISHED;
                result.add(String.format("当前步骤已超过最大步骤数，请重新提问 maxStep (%s)", maxSteps));
            }
        } catch (Exception e) {
            state = AgentStateEnum.ERROR;
            log.error("Agent 执行异常", e);
            return String.format("Agent 执行异常：%s", e.getMessage());
        }
        return String.join("\n", result);
    }

    public SseEmitter runByStream(String conversationId, String userPrompt) {
        try {
            run(conversationId, userPrompt);
            finalSummaryByStream(userPrompt);
        } catch (Exception e) {
            log.error("Agent 执行异常", e);
        } finally {
            clear();
        }
        return sseEmitter;
    }

    public String runBySync(String conversationId, String userPrompt){
        run(conversationId, userPrompt);
        return finalSummaryBySync(userPrompt);
    }

    public void finalSummaryByStream(String userRawInput) {
        Long userId = StpUtil.getLoginIdAsLong();
        // 发送思考过程完成的提示
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("thinking")
                    .data("思考完成，正在生成总结..."));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 构建总结上下文：包含完整的思考和执行过程
        StringBuilder summaryContext = new StringBuilder();
        summaryContext.append("【用户原始问题】\n").append(userRawInput).append("\n\n");
        summaryContext.append("【Agent执行过程】\n");
        
        // 提取并格式化 agent 的思考内容
        for (int i = 0; i < getChatMessage().size(); i++) {
            Message msg = getChatMessage().get(i);
            if (msg instanceof AssistantMessage assistantMsg) {
                // 记录 AI 的思考和决策
                if (assistantMsg.getToolCalls() != null && !assistantMsg.getToolCalls().isEmpty()) {
                    summaryContext.append("- 决策：调用工具 ");
                    assistantMsg.getToolCalls().forEach(toolCall -> 
                        summaryContext.append(String.format("[%s]", toolCall.name()))
                    );
                    summaryContext.append("\n");
                }
                if (StringUtils.isNotBlank(assistantMsg.getText())) {
                    summaryContext.append("- 思考：").append(assistantMsg.getText()).append("\n");
                }
            } else if (msg instanceof ToolResponseMessage toolResponseMsg) {
                // 记录工具执行结果
                toolResponseMsg.getResponses().forEach(response -> {
                    summaryContext.append(String.format("- 工具 [%s] 执行结果：%s\n", 
                        response.name(), 
                        response.responseData()));
                });
            }
        }
        
        // 添加总结提示词，并附带格式化的执行过程
        String finalPrompt = getFinalSummaryPrompt() + "\n\n" + summaryContext.toString();
        getChatMessage().add(new UserMessage(finalPrompt));
        
        Prompt prompt = new Prompt(getChatMessage());
        Flux<ChatResponse> content = chatClient.prompt(prompt)
                .stream()
                .chatResponse();
        // 使用带 ChatResponse 的方法，以便获取 Token 统计信息
        AtomicReference<Usage> usageRef = new AtomicReference<>();
        StringBuilder text = new StringBuilder();
        content.subscribe(
                        chatResponse -> {
                            try {
                                String chunk = chatResponse.getResult().getOutput().getText();
                                // ✅ 使用自定义事件类型 "content" 发送总结内容
                                sseEmitter.send(SseEmitter.event()
                                        .name("content")
                                        .data(chunk));
                                text.append(chunk);
                                if (chatResponse.getMetadata() != null && chatResponse.getMetadata().getUsage() != null){
                                    usageRef.set(chatResponse.getMetadata().getUsage());
                                }
                            } catch (IOException e) {
                                sseEmitter.completeWithError(e);
                            }
                        },
                        sseEmitter::completeWithError,
                        sseEmitter::complete
                );
        sseEmitter.onCompletion(() -> {
            // 保存到聊天记录（只保存原始问题和最终答案，不保存中间过程）
            Long token = getUsedToken() + usageRef.get().getTotalTokens();
            TokenStatisticsService tokenStatisticsService = (TokenStatisticsService) SpringContextUtil.getBean("tokenStatisticsService");
            tokenStatisticsService.updateUserToken(userId, token);
            pgChatMemory.add(getCurrentConversationId(), new UserMessage(userRawInput));
            pgChatMemory.add(getCurrentConversationId(), new AssistantMessage(text.toString()));
        });
    }

    public String finalSummaryBySync(String userRawInput) {
        // 构建总结上下文：包含完整的思考和执行过程
        StringBuilder summaryContext = new StringBuilder();
        summaryContext.append("【用户原始问题】\n").append(userRawInput).append("\n\n");
        summaryContext.append("【Agent执行过程】\n");
        
        // 提取并格式化 agent 的思考内容
        for (int i = 0; i < getChatMessage().size(); i++) {
            Message msg = getChatMessage().get(i);
            if (msg instanceof AssistantMessage assistantMsg) {
                // 记录 AI 的思考和决策
                if (assistantMsg.getToolCalls() != null && !assistantMsg.getToolCalls().isEmpty()) {
                    summaryContext.append("- 决策：调用工具 ");
                    assistantMsg.getToolCalls().forEach(toolCall -> 
                        summaryContext.append(String.format("[%s]", toolCall.name()))
                    );
                    summaryContext.append("\n");
                }
                if (StringUtils.isNotBlank(assistantMsg.getText())) {
                    summaryContext.append("- 思考：").append(assistantMsg.getText()).append("\n");
                }
            } else if (msg instanceof ToolResponseMessage toolResponseMsg) {
                // 记录工具执行结果
                toolResponseMsg.getResponses().forEach(response -> {
                    summaryContext.append(String.format("- 工具 [%s] 执行结果：%s\n", 
                        response.name(), 
                        response.responseData()));
                });
            }
        }
        
        // 添加总结提示词，并附带格式化的执行过程
        String finalPrompt = getFinalSummaryPrompt() + "\n\n" + summaryContext.toString();
        getChatMessage().add(new UserMessage(finalPrompt));
        
        // 调用 LLM 生成最终总结
        Prompt prompt = new Prompt(getChatMessage());
        String text = chatClient.prompt(prompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
        
        // 保存到聊天记录（只保存原始问题和最终答案，不保存中间过程）
        pgChatMemory.add(getCurrentConversationId(), new UserMessage(userRawInput));
        pgChatMemory.add(getCurrentConversationId(), new AssistantMessage(text));
        
        return text;
    }


    public abstract String step();

    public abstract void clear();
}
