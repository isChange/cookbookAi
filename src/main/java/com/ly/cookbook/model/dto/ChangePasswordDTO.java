package com.ly.cookbook.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改密码请求DTO
 *
 * @author admin
 * @date 2025-10-11
 */
@Data
@Schema(description = "修改密码请求")
public class ChangePasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "原密码", required = true, example = "oldPassword123")
    private String oldPassword;

    @Schema(description = "新密码", required = true, example = "newPassword123")
    private String newPassword;

    @Schema(description = "确认新密码", required = true, example = "newPassword123")
    private String confirmPassword;
}

