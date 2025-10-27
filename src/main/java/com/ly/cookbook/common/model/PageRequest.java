package com.ly.cookbook.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/27 23:02
 * @email liuyia2022@163.com
 */
@Data
public class PageRequest {
    @Schema(description = "当前页", example = "1")
    private int current = 1;

    @Schema(description = "页面大小", example = "10")
    private int pageSize = 10;

    @Schema(description = "总数量", example = "0")
    private int total = 0;

    @Schema(description = "排序列", example = "createTime")
    private String sortField;

    @Schema(description = "排序的方向desc或者asc", example = "descend")
    private String sortOrder = "descend";


}
