package com.ly.cookbook.config;

import com.ly.cookbook.aitools.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description AI工具配置类，负责注册所有的工具回调
 * @createDate：2025/10/4 20:31
 * @email liuyia2022@163.com
 */
@Configuration
@Slf4j
public class AiToolsConfiguration {

    @Value("${serpapi.key}")
    private String serpApiKey;

    @Value("${serpapi.engine}")
    private String searchEngine;

    @Bean
    public ToolCallback[] allTools() {
        log.info("注册 AI 工具，SerpAPI Key: {}", serpApiKey != null && !serpApiKey.isEmpty() ? "已配置" : "未配置");
        return ToolCallbacks.from(
                new WebScriptTools(),
                new WebSearchTools(serpApiKey, searchEngine),
                new FileTools(),
                new ResourceDownloadTools(),
                new TerminalOperationTools(),
                new TerminateTools()
        );
    }
}
