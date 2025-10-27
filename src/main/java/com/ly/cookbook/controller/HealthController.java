package com.ly.cookbook.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.ly.cookbook.common.constant.UserConstant;
import com.ly.cookbook.enums.UserRoleEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description 健康检查接口
 * @createDate：2025/9/13 18:25
 * @email liuyia2022@163.com
 */
@RestController
@RequestMapping("/health")
@Tag(name = "健康检查接口")
public class HealthController {

    @GetMapping
    @Operation(summary = "健康检查接口")
    @SaCheckRole("ADMIN")
    public String healthCheck() {
        return "ok";
    }
}

