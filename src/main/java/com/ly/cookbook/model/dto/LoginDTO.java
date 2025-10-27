package com.ly.cookbook.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求DTO
 *
 * @author admin
 * @date 2025-10-11
 */
@Data
@Schema(description = "用户登录请求")
public class LoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名", required = true, example = "admin")
    private String username;

    @Schema(description = "密码", required = true, example = "admin123")
    private String password;

    @Schema(description = "记住我（7天免登录）", example = "false")
    private Boolean rememberMe = false;
}

