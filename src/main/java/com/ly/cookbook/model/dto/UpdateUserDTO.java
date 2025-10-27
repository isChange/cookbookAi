package com.ly.cookbook.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新用户信息请求DTO
 *
 * @author admin
 * @date 2025-10-11
 */
@Data
@Schema(description = "更新用户信息请求")
public class UpdateUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "昵称", example = "新昵称")
    private String nickname;

    @Schema(description = "电子邮箱", example = "newemail@example.com")
    private String email;

    @Schema(description = "手机号码", example = "13900139999")
    private String phone;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;
}

