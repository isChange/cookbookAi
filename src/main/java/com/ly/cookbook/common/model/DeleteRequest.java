package com.ly.cookbook.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/27 23:01
 * @email liuyia2022@163.com
 */
@Data
public class DeleteRequest implements Serializable {
    private static final long serialVersionUID = -3276119614663597922L;
    @Schema(description = "ID")
    private Long id;

}
