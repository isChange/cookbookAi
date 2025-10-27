package com.ly.cookbook.aitools;

import org.springframework.ai.tool.annotation.Tool;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/10/4 23:01
 * @email liuyia2022@163.com
 */
public class TerminateTools {
    @Tool(description = "Terminate the interaction when the request is met OR if the assistant cannot proceed further with the task. " +
            "When you have finished all the tasks, call this tool to end the work.")
    public String doTerminate() {
        return "任务结束";
    }
}
