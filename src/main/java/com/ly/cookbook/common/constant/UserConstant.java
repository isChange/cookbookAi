package com.ly.cookbook.common.constant;

import com.ly.cookbook.enums.UserRoleEnum;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/29 10:16
 * @email liuyia2022@163.com
 */
public class UserConstant {
    //*************************** 系统角色信息 ***************************
    public final static String USER_ADMIN = UserRoleEnum.ADMIN.getCode();
    public final static String USER_FREE = UserRoleEnum.FREE_USER.getCode();
    public final static String USER_VIP = UserRoleEnum.VIP_USER.getCode();
}
