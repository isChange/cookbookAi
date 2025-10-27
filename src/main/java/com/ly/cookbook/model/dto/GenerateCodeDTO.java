package com.ly.cookbook.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 生成注册码 DTO
 * 说明: 由内部App用户生成注册凭证（类似邀请码）
 *
 * @author admin
 * @date 2025-10-14
 */
@Data
@Schema(description = "生成注册码请求")
public class GenerateCodeDTO {

    @Schema(description = "备注说明（可选）", example = "给新员工使用")
    private String remark;

    @Min(value = 1, message = "批量生成数量至少为1")
    @Max(value = 100, message = "批量生成数量最多为100")
    @Schema(description = "批量生成数量（可选，默认1）", example = "1", minimum = "1", maximum = "100")
    private Integer count;
}

