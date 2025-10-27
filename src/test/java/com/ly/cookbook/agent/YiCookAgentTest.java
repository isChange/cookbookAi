package com.ly.cookbook.agent;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class YiCookAgentTest {
    @Resource
    private DashScopeChatModel dashScopeChatModel;
    @Resource
    private ToolCallback[] allTools;
    @Resource
    private ChatMemory pgChatMemory;
    @Resource
    private Advisor chatLogAdvisor;

    @Test
    void test() {
        YiCookAgent yiCookAgent = new YiCookAgent(allTools, dashScopeChatModel, pgChatMemory, chatLogAdvisor);
        String run = yiCookAgent.run("2", "我想吃烧烤，但是我不知道怎么做，并且我需要看一些图片来增加我做烧烤的兴趣");
        System.out.println(run);
    }
}