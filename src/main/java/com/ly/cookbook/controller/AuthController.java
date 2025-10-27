package com.ly.cookbook.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ly.cookbook.common.model.Result;
import com.ly.cookbook.model.dto.LoginDTO;
import com.ly.cookbook.model.vo.LoginVO;
import com.ly.cookbook.model.vo.UserInfoVO;
import com.ly.cookbook.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author admin
 * @date 2025-10-11
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、登出相关接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口，支持记住我功能")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        log.info("用户登录请求，用户名：{}", loginDTO.getUsername());
        LoginVO loginVO = authService.login(loginDTO);
        return Result.<LoginVO>success(loginVO);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出接口")
    public Result<String> logout() {
        authService.logout();
        return Result.<String>success("登出成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前登录用户信息", description = "获取当前登录用户的详细信息")
    public Result<UserInfoVO> getCurrentUserInfo() {
        UserInfoVO userInfo = authService.getCurrentUserInfo();
        return Result.<UserInfoVO>success(userInfo);
    }

    /**
     * 检查登录状态
     */
    @GetMapping("/check")
    @Operation(summary = "检查登录状态", description = "检查用户是否已登录")
    public Result<Boolean> checkLogin() {
        boolean isLogin = authService.isLogin();
        return Result.<Boolean>success(isLogin);
    }

    /**
     * 获取Token信息
     */
    @GetMapping("/token/info")
    @Operation(summary = "获取Token信息", description = "获取当前Token的详细信息")
    public Result<Object> getTokenInfo() {
        if (!StpUtil.isLogin()) {
            return Result.<Object>success("未登录");
        }
        
        return Result.<Object>success(StpUtil.getTokenInfo());
    }
}

