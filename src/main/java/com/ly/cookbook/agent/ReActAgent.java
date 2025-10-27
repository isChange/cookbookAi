package com.ly.cookbook.agent;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/10/3 21:07
 * @email liuyia2022@163.com
 */
public abstract class ReActAgent extends BaseAgent{


    @Override
    public String step() {
        if (think()) {
            return act();
        }
        return "思考完成 - 无需行动";
    }

    public abstract boolean think();

    public abstract String act();


}
