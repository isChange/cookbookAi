package com.ly.cookbook.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 验证注册码 DTO
 * 说明: 用于用户注册时验证注册码
 *
 * @author admin
 * @date 2025-10-14
 */
@Data
@Schema(description = "验证注册码请求")
public class ValidateCodeDTO {

    @NotBlank(message = "注册码不能为空")
    @Schema(description = "注册码", example = "REG-A1B2C3D4E5F6")
    private String code;
}

