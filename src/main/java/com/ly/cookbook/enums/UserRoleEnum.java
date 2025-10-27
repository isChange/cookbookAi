package com.ly.cookbook.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举
 *
 * @author admin
 * @date 2025-10-11
 */
@Getter
@AllArgsConstructor
public enum UserRoleEnum {

    /**
     * 管理员
     */
    ADMIN("ADMIN", "管理员"),

    /**
     * 免费用户
     */
    FREE_USER("FREE_USER", "免费用户"),

    /**
     * VIP用户
     */
    VIP_USER("VIP_USER", "VIP用户");

    private final String code;
    private final String desc;

    /**
     * 根据code获取枚举
     */
    public static UserRoleEnum getByCode(String code) {
        for (UserRoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

