package com.ly.cookbook.exception.emun;

import com.ly.cookbook.exception.error.ErrorType;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description AI菜谱智能体错误枚举
 * @createDate：2025/10/3 18:17
 * @email liuyia2022@163.com
 */
public enum AgentErrorEnum implements ErrorType {

    // ========== AI服务相关错误 (2000-2099) ==========
    AI_SERVICE_ERROR(2000, "AI服务调用失败，请稍后重试", "AI service call failed, please try again later"),
    AI_RESPONSE_TIMEOUT(2001, "AI服务响应超时，请稍后重试", "AI service response timeout, please try again later"),
    AI_RESPONSE_EMPTY(2002, "AI返回内容为空，请重新提问", "AI response is empty, please ask again"),
    AI_TOKEN_INSUFFICIENT(2003, "AI服务配额不足，请联系管理员", "AI service quota insufficient, please contact administrator"),
    AI_SERVICE_UNAVAILABLE(2004, "AI服务暂时不可用，请稍后重试", "AI service is temporarily unavailable, please try again later"),
    AI_RESPONSE_PARSE_ERROR(2005, "AI响应解析失败", "AI response parsing failed"),
    AI_MODEL_ERROR(2006, "AI模型错误，请联系技术支持", "AI model error, please contact technical support"),

    // ========== 输入参数相关错误 (2100-2199) ==========
    INPUT_EMPTY(2100, "输入内容不能为空", "Input content cannot be empty"),
    INPUT_TOO_LONG(2101, "输入内容过长，请精简后重试", "Input content is too long, please simplify and try again"),
    INVALID_INGREDIENTS(2102, "食材信息无效，请检查后重试", "Invalid ingredient information, please check and try again"),
    INVALID_PARAMETER(2103, "参数格式错误", "Invalid parameter format"),
    SENSITIVE_WORD_DETECTED(2104, "输入内容包含敏感词，请修改后重试", "Input contains sensitive words, please modify and try again"),
    RECIPE_NAME_EMPTY(2105, "菜谱名称不能为空", "Recipe name cannot be empty"),
    INVALID_SERVINGS(2106, "用餐人数必须大于0", "Number of servings must be greater than 0"),
    INVALID_COOKING_TIME(2107, "烹饪时间设置不合理", "Cooking time setting is unreasonable"),
    INVALID_DIFFICULTY(2108, "难度级别无效", "Invalid difficulty level"),
    INVALID_MEAL_TYPE(2109, "餐次类型无效", "Invalid meal type"),

    // ========== 会话相关错误 (2200-2299) ==========
    CONVERSATION_NOT_FOUND(2200, "会话不存在", "Conversation not found"),
    CONVERSATION_EXPIRED(2201, "会话已过期，请开始新的对话", "Conversation has expired, please start a new conversation"),
    CONVERSATION_LOAD_FAILED(2202, "会话记忆加载失败", "Failed to load conversation memory"),
    CONVERSATION_CONTEXT_TOO_LONG(2203, "会话上下文过长，建议开始新的对话", "Conversation context is too long, suggest starting a new conversation"),
    CONVERSATION_ID_EMPTY(2204, "会话ID不能为空", "Conversation ID cannot be empty"),
    CONVERSATION_SAVE_FAILED(2205, "会话保存失败", "Failed to save conversation"),
    CONVERSATION_DELETE_FAILED(2206, "会话删除失败", "Failed to delete conversation"),

    // ========== 菜谱相关错误 (2300-2399) ==========
    RECIPE_NOT_FOUND(2300, "未找到相关菜谱", "Recipe not found"),
    NO_MATCHING_RECIPE(2301, "根据当前条件未找到匹配的菜谱，请调整条件后重试", "No matching recipe found, please adjust conditions and try again"),
    INVALID_INGREDIENT_COMBINATION(2302, "食材搭配不合理或存在禁忌", "Invalid or conflicting ingredient combination"),
    RECIPE_GENERATION_FAILED(2303, "菜谱生成失败，请重试", "Recipe generation failed, please try again"),
    RECIPE_DETAIL_NOT_FOUND(2304, "菜谱详情不存在", "Recipe details not found"),
    RECIPE_CATEGORY_INVALID(2305, "菜谱分类无效", "Invalid recipe category"),
    INSUFFICIENT_INGREDIENTS(2306, "提供的食材不足以制作菜品", "Insufficient ingredients to make a dish"),
    RECIPE_ALREADY_EXISTS(2307, "该菜谱已存在", "Recipe already exists"),

    // ========== 用户相关错误 (2400-2499) ==========
    USER_PREFERENCE_LOAD_FAILED(2400, "用户偏好加载失败", "Failed to load user preferences"),
    USER_PREFERENCE_SAVE_FAILED(2401, "用户偏好保存失败", "Failed to save user preferences"),
    FAVORITE_ADD_FAILED(2402, "添加收藏失败", "Failed to add to favorites"),
    FAVORITE_DELETE_FAILED(2403, "删除收藏失败", "Failed to delete from favorites"),
    FAVORITE_ALREADY_EXISTS(2404, "该菜谱已在收藏中", "Recipe already in favorites"),
    FAVORITE_NOT_FOUND(2405, "收藏不存在", "Favorite not found"),
    USER_REQUEST_TOO_FREQUENT(2406, "请求过于频繁，请稍后再试", "Too many requests, please try again later"),
    USER_ID_EMPTY(2407, "用户ID不能为空", "User ID cannot be empty"),
    USER_TOKEN_EMPTY(2408, "用户无Token使用", "User token be empty"),

    // ========== 数据库相关错误 (2500-2599) ==========
    DATABASE_CONNECTION_FAILED(2500, "数据库连接失败", "Database connection failed"),
    DATABASE_SAVE_FAILED(2501, "数据保存失败", "Failed to save data"),
    DATABASE_QUERY_FAILED(2502, "数据查询失败", "Failed to query data"),
    DATABASE_UPDATE_FAILED(2503, "数据更新失败", "Failed to update data"),
    DATABASE_DELETE_FAILED(2504, "数据删除失败", "Failed to delete data"),
    DATA_NOT_FOUND(2505, "数据不存在", "Data not found"),

    // ========== 知识库相关错误 (2600-2699) ==========
    INGREDIENT_LIBRARY_NOT_FOUND(2600, "食材库信息未找到", "Ingredient library information not found"),
    COOKING_TIP_NOT_FOUND(2601, "烹饪技巧未找到", "Cooking tip not found"),
    NUTRITION_INFO_NOT_AVAILABLE(2602, "营养信息暂不可用", "Nutrition information not available"),
    KNOWLEDGE_BASE_LOAD_FAILED(2603, "知识库加载失败", "Failed to load knowledge base"),

    // ========== 业务逻辑错误 (2700-2799) ==========
    MENU_PLANNING_FAILED(2700, "菜单规划失败", "Menu planning failed"),
    NUTRITION_ANALYSIS_FAILED(2701, "营养分析失败", "Nutrition analysis failed"),
    INGREDIENT_SUBSTITUTION_NOT_FOUND(2702, "未找到合适的食材替代方案", "No suitable ingredient substitution found"),
    DIETARY_RESTRICTION_CONFLICT(2703, "饮食限制冲突", "Dietary restriction conflict"),
    SHOPPING_LIST_GENERATION_FAILED(2704, "购物清单生成失败", "Shopping list generation failed"),

    // ========== 系统相关错误 (2800-2899) ==========
    SYSTEM_BUSY(2800, "系统繁忙，请稍后重试", "System is busy, please try again later"),
    SYSTEM_MAINTENANCE(2801, "系统维护中，请稍后访问", "System under maintenance, please visit later"),
    INTERNAL_SERVER_ERROR(2802, "服务器内部错误", "Internal server error"),
    PERMISSION_DENIED(2803, "权限不足", "Permission denied"),
    RESOURCE_NOT_FOUND(2804, "资源不存在", "Resource not found"),
    UNSUPPORTED_OPERATION(2805, "不支持的操作", "Unsupported operation"),

    // ========== 配置相关错误 (2900-2999) ==========
    CONFIG_ERROR(2900, "配置错误", "Configuration error"),
    API_KEY_INVALID(2901, "API密钥无效", "Invalid API key"),
    API_KEY_EXPIRED(2902, "API密钥已过期", "API key has expired"),
    RATE_LIMIT_EXCEEDED(2903, "请求次数超过限制", "Rate limit exceeded"),
    ;

    private final Integer code;
    private final String cnMessage;
    private final String usMessage;

    AgentErrorEnum(Integer code, String cnMessage, String usMessage) {
        this.code = code;
        this.cnMessage = cnMessage;
        this.usMessage = usMessage;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getCnMessage() {
        return cnMessage;
    }

    @Override
    public String getUsMessage() {
        return usMessage;
    }

    /**
     * 根据错误码获取错误枚举
     *
     * @param code 错误码
     * @return 错误枚举，未找到则返回null
     */
    public static AgentErrorEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AgentErrorEnum error : AgentErrorEnum.values()) {
            if (error.getCode().equals(code)) {
                return error;
            }
        }
        return null;
    }
}
