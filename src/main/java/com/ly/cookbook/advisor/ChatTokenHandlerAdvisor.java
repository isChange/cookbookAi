package com.ly.cookbook.advisor;

import cn.dev33.satoken.stp.StpUtil;
import com.ly.cookbook.model.User;
import com.ly.cookbook.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/10/20 18:34
 * @email liuyia2022@163.com
 */
@Component
@Slf4j
public class ChatTokenHandlerAdvisor implements CallAdvisor, StreamAdvisor {
    @Resource
    UserService userService;
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        handlerToken(chatClientResponse);
        return chatClientResponse;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        // 【重要】流式场景下无法使用 SaToken ThreadLocal 上下文
        // Token 统计移至 Controller 层的 SSE 完成回调中处理
        log.debug("流式请求，跳过 Token 统计（由 Controller 层处理）");
        return streamAdvisorChain.nextStream(chatClientRequest);
    }

    private void handlerToken(ChatClientResponse chatClientResponse) {
        // 同步调用版本（adviseCall 使用）
        try {
            Long userid = StpUtil.getLoginIdAsLong();
            handlerToken(chatClientResponse, userid);
        } catch (Exception e) {
            log.warn("无法获取登录用户ID，跳过 Token 统计", e);
        }
    }

    private void handlerToken(ChatClientResponse chatClientResponse, Long userid) {
        //统计Token
        Usage usage = chatClientResponse.chatResponse().getMetadata().getUsage();
        //更新用户用户token
        User user = userService.getById(userid);
        if (user != null){
            long newUsedToken = user.getUsedToken() + usage.getTotalTokens().longValue();
            userService.lambdaUpdate().eq(User::getId, userid).set(User::getUsedToken, newUsedToken).update();
            log.info("用户更新Token成功! userid: {}, 消费token: {}", userid, usage.getTotalTokens());
        }
    }

    @Override
    public String getName() {
        return "chatTokenHandlerAdvisor";
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
