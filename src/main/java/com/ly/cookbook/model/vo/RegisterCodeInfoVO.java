package com.ly.cookbook.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册码详细信息 VO
 *
 * @author admin
 * @date 2025-10-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "注册码详细信息")
public class RegisterCodeInfoVO {

    @Schema(description = "注册码", example = "REG-ABC123XYZ456")
    private String code;

    @Schema(description = "生成者用户ID", example = "1001")
    private Long generatorUserId;

    @Schema(description = "生成者用户名", example = "admin")
    private String generatorUsername;

    @Schema(description = "备注说明", example = "给新员工使用")
    private String remark;

    @Schema(description = "有效期（秒）", example = "86400")
    private Long expireTime;

    @Schema(description = "剩余有效时间（秒）", example = "85800")
    private Long remainingTime;

    @Schema(description = "生成时间", example = "2024-10-14 12:00:00")
    private String generateTime;

    @Schema(description = "是否已使用", example = "false")
    private Boolean used;
}

