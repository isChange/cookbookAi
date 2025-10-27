package com.ly.cookbook.exception.emun;

import com.ly.cookbook.exception.error.ErrorType;

/**
 * 注册码相关错误枚举
 *
 * @author admin
 * @date 2025-10-14
 */
public enum RegisterCodeErrorEnum implements ErrorType {

    // ========== 注册码生成相关错误 (3000-3099) ==========
    GENERATOR_USER_ID_EMPTY(3000, "生成者用户ID不能为空", "Generator user ID cannot be empty"),
    REGISTER_CODE_GENERATE_FAILED(3001, "注册码生成失败", "Failed to generate register code"),
    BATCH_COUNT_INVALID(3002, "批量生成数量必须在 1-100 之间", "Batch count must be between 1 and 100"),
    REGISTER_CODE_LIMIT_EXCEEDED(3003, "注册码生成数量已达上限", "Register code generation limit exceeded"),
    
    // ========== 注册码验证相关错误 (3100-3199) ==========
    REGISTER_CODE_EMPTY(3100, "注册码不能为空", "Register code cannot be empty"),
    REGISTER_CODE_NOT_FOUND(3101, "注册码不存在或已过期", "Register code not found or expired"),
    REGISTER_CODE_ALREADY_USED(3102, "注册码已被使用", "Register code has already been used"),
    REGISTER_CODE_EXPIRED(3103, "注册码已过期", "Register code has expired"),
    REGISTER_CODE_INVALID_FORMAT(3104, "注册码格式不正确", "Invalid register code format"),
    
    // ========== 注册码删除相关错误 (3200-3299) ==========
    REGISTER_CODE_DELETE_FAILED(3200, "注册码删除失败", "Failed to delete register code"),
    REGISTER_CODE_NOT_EXIST(3201, "注册码不存在", "Register code does not exist"),
    
    // ========== 注册码查询相关错误 (3300-3399) ==========
    REGISTER_CODE_QUERY_FAILED(3300, "注册码查询失败", "Failed to query register code"),
    USER_ID_EMPTY(3301, "用户ID不能为空", "User ID cannot be empty"),
    ;

    private final Integer code;
    private final String cnMessage;
    private final String usMessage;

    RegisterCodeErrorEnum(Integer code, String cnMessage, String usMessage) {
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
    public static RegisterCodeErrorEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (RegisterCodeErrorEnum error : RegisterCodeErrorEnum.values()) {
            if (error.getCode().equals(code)) {
                return error;
            }
        }
        return null;
    }
}

