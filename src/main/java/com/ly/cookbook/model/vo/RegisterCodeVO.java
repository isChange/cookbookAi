package com.ly.cookbook.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 注册码响应 VO
 * 说明: 生成注册码后的响应信息
 *
 * @author admin
 * @date 2025-10-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "注册码响应")
public class RegisterCodeVO {

    @Schema(description = "注册码", example = "REG-A1B2C3D4E5F6")
    private String code;

    @Schema(description = "注册码列表（批量生成时）", example = "[\"REG-A1B2C3D4E5F6\", \"REG-B2C3D4E5F6A1\"]")
    private List<String> codes;

    @Schema(description = "生成者用户ID", example = "1001")
    private Long generatorUserId;

    @Schema(description = "备注说明", example = "给新员工使用")
    private String remark;

    @Schema(description = "有效期（秒）", example = "86400")
    private Long expireTime;

    @Schema(description = "生成时间", example = "2024-10-14 12:00:00")
    private String generateTime;
}

