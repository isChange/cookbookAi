package com.ly.cookbook.advisor;

import cn.dev33.satoken.stp.StpUtil;
import com.ly.cookbook.model.User;
import com.ly.cookbook.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;


/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/10/2 22:07
 * @email liuyia2022@163.com
 */
@Component
@Slf4j
public class ChatLogAdvisor implements CallAdvisor, StreamAdvisor {

    private ChatClientRequest before(ChatClientRequest chatClientRequest) {
        UserMessage userMessage = chatClientRequest.prompt().getUserMessage();
        String text = userMessage.getText();
        log.info("🚀 开始处理用户问题: {}", text);
        return chatClientRequest;
    }
    private ChatClientResponse after(ChatClientResponse chatClientResponse) {
        Generation result = chatClientResponse.chatResponse().getResult();
        AssistantMessage output = result.getOutput();
        log.info("🚀 响应元数据: {} ,响应结果: {}", output.getText(), output.getMetadata());
        //统计Token
        Usage usage = chatClientResponse.chatResponse().getMetadata().getUsage();
        log.info("🚀提示词消耗Token: {} Ai回答消耗Token: {} 总计消耗Token: {}", usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
        return chatClientResponse;
    }



    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        before(chatClientRequest);
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        after(chatClientResponse);
        return chatClientResponse;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        before(chatClientRequest);
        Flux<ChatClientResponse> chatClientResponseFlux = streamAdvisorChain.nextStream(chatClientRequest);
        return new ChatClientMessageAggregator().aggregateChatClientResponse(chatClientResponseFlux, this::after);
    }

    @Override
    public String getName() {
        return "chatLogAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
