package com.ly.cookbook.agent;

import com.ly.cookbook.advisor.ChatLogAdvisor;
import com.ly.cookbook.common.units.SpringContextUtil;
import com.ly.cookbook.enums.AgentStateEnum;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/10/4 22:57
 * @email liuyia2022@163.com
 */
public class YiCookAgent extends ToolCallAgent {


    public YiCookAgent(ToolCallback[] availableTools, ChatModel chatModel, ChatMemory chatMemory, Advisor ...advisors) {
        super(availableTools);
        //设置LLM客户端
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(advisors)
                .build();
        setChatClient(chatClient);
        setPgChatMemory(chatMemory);
        //设置名称
        this.setName("YiCookAgent");
        //设置系统提示
        String SYSTEM_PROMPT = """  
                You are YiCookAgent, an all-capable AI assistant, aimed at solving any task presented by the user.  
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.  
                """;
        setSystemPrompt(SYSTEM_PROMPT);
        //设置下一步提示
        String NEXT_STEP_PROMPT = """  
                Based on user needs, proactively select the most appropriate tool or combination of tools.  
                For complex tasks, you can break down the problem and use different tools step by step to solve it.  
                After using each tool, clearly explain the execution results and suggest the next steps.  
                If you want to stop the interaction at any point, use the `terminate` tool/function call.  
                """;
        setNextStepPrompt(NEXT_STEP_PROMPT);

        String FINAL_SUMMARY_PROMPT = """
                你是一个专业的菜谱总结专家和烹饪技巧专家。请根据用户输入的核心内容，智能判断其需求是期望获得完整菜谱还是专项技巧，并以此为主导，采用最合适的格式进行总结。
                
                  【核心指令】
    
                  信息提炼：提取核心信息，去除重复和冗余。
    
                  格式基准：以标准的完整菜谱格式为基准框架。
    
                  智能微调：
    
                  当内容明显偏向于某个专项技巧（如“如何出沙”、“怎样嫩肉”）时，应在标准框架内强化“烹饪小贴士”部分，或将其提升为“核心技术”模块。
    
                  对于完整菜谱，严格遵循基准格式。
    
                  对于纯粹的技巧问答，可将“制作步骤”模块灵活调整为“操作步骤”或“原理解析”。
    
                  表达风格：语言通俗易懂、步骤具体明确、风格亲切友好，便于家庭烹饪。
    
                  【输出格式 - 基准框架】
    
                  🍳 【菜名/技巧主题】
                  简要介绍这道菜的风味特点，或该项技巧的用途与价值。
    
                  🥗 【食材清单】 (对于完整菜谱)
    
                  主料：[具体用量]
    
                  辅料：[具体用量]
    
                  调味料：[具体用量]
    
                  👩‍🍳 【制作步骤 / 操作步骤】 (根据内容灵活命名)
    
                  第一步具体操作（包含关键细节，如火候、状态描述）。
    
                  第二步具体操作。
    
                  ...（依此类推）
    
                  💡 【烹饪小贴士 / 核心技术】 (根据内容重点灵活命名与强化)
    
                  核心技巧：[针对用户问题中的难点，重点阐述其原理与操作方法]。
    
                  注意事项：[提醒常见失败点与安全事项]。
    
                  举一反三：[提供食材替代方案或该技巧的其他应用场景]。
                """;
        setFinalSummaryPrompt(FINAL_SUMMARY_PROMPT);

        setSseEmitter(new SseEmitter(1000000L));
    }

    @Override
    public void clear() {
        setState(AgentStateEnum.IDLE);
        setCurrentStep(0);
        getChatMessage().clear();
        setToolCallChatResponse(null);
    }
}
