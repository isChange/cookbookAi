package com.ly.cookbook;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.ly.cookbook.agent.YiCookAgent;
import com.ly.cookbook.common.units.PasswordUtil;
import com.ly.cookbook.service.SpringAiChatMemoryService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CookbookApplicationTests {
    @Resource
    private SpringAiChatMemoryService chetMemoryService;

    @Resource
    private DashScopeChatModel dashScopeChatModel;
    @Resource
    private Advisor chatLogAdvisor;

    @Resource
    private Advisor ragCloudAdvisor;
    @Test
    void contextLoads() {
        chetMemoryService.list().forEach(text -> System.out.println(text.getConversationId()));
    }

    @Test
    void pwdUtils(){
        ChatClient chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(ragCloudAdvisor)
                .defaultAdvisors(chatLogAdvisor)
                .build();
        String content = chatClient.prompt()
                .user("砂锅蒜蓉粉丝虾怎么做")
                .call()
                .content();
        System.out.println(content);
    }
}
