package com.ly.cookbook.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录状态枚举
 *
 * @author admin
 * @date 2025-10-11
 */
@Getter
@AllArgsConstructor
public enum LoginStatusEnum {

    /**
     * 登录失败
     */
    FAILURE(0, "登录失败"),

    /**
     * 登录成功
     */
    SUCCESS(1, "登录成功"),

    /**
     * 登出
     */
    LOGOUT(2, "登出");

    private final Integer code;
    private final String desc;

    /**
     * 根据code获取枚举
     */
    public static LoginStatusEnum getByCode(Integer code) {
        for (LoginStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}

