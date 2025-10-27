package com.ly.cookbook.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ly.cookbook.common.model.Result;
import com.ly.cookbook.model.dto.ChangePasswordDTO;
import com.ly.cookbook.model.dto.RegisterDTO;
import com.ly.cookbook.model.dto.UpdateUserDTO;
import com.ly.cookbook.model.vo.TokenInfoVO;
import com.ly.cookbook.model.vo.UserInfoVO;
import com.ly.cookbook.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 *
 * @author admin
 * @date 2025-10-11
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户注册、信息修改、密码管理等接口")
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册接口，默认注册为免费用户")
    public Result<UserInfoVO> register(@RequestBody RegisterDTO registerDTO) {
        log.info("用户注册请求，用户名：{}", registerDTO.getUsername());
        UserInfoVO userInfo = userService.register(registerDTO);
        return Result.<UserInfoVO>success(userInfo);
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping("/update")
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的基本信息")
    public Result<UserInfoVO> updateUserInfo(@RequestBody UpdateUserDTO updateUserDTO) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("用户更新信息请求，用户ID：{}", userId);
        UserInfoVO userInfo = userService.updateUserInfo(userId, updateUserDTO);
        return Result.<UserInfoVO>success(userInfo);
    }

    /**
     * 修改密码
     */
    @PutMapping("/change-password")
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    public Result<String> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("用户修改密码请求，用户ID：{}", userId);
        userService.changePassword(userId, changePasswordDTO);
        return Result.<String>success("密码修改成功");
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    @Operation(summary = "检查用户名是否可用", description = "注册前检查用户名是否已被使用")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.checkUsernameExists(username);
        // 返回是否可用（取反）
        return Result.<Boolean>success(!exists);
    }

    /**
     * 检查用户Token是否可用
     */
    @GetMapping("/check-token")
    @Operation(summary = "检查用户Token是否可用", description = "检查用户Token是否可用")
    public Result<Boolean> checkToken() {
        return Result.success(userService.checkUserToken(StpUtil.getLoginIdAsLong()));
    }
    /**
     * 获取用户Token信息
     */
    @GetMapping("/token/info")
    @Operation(summary = "获取Token信息", description = "获取当前Token的详细信息")
    public Result<TokenInfoVO> getTokenInfo() {
        return Result.success(userService.getUserTokenInfo(StpUtil.getLoginIdAsLong()));
    }
}

