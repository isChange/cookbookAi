package com.ly.cookbook.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求DTO
 *
 * @author admin
 * @date 2025-10-11
 */
@Data
@Schema(description = "用户注册请求")
public class RegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名", required = true, example = "newuser")
    private String username;

    @Schema(description = "密码", required = true, example = "123456")
    private String password;

    @Schema(description = "确认密码", required = true, example = "123456")
    private String confirmPassword;

    @Schema(description = "昵称", example = "新用户")
    private String nickname;

    @Schema(description = "电子邮箱", example = "newuser@example.com")
    private String email;

    @Schema(description = "手机号码", example = "13800138888")
    private String phone;
}

