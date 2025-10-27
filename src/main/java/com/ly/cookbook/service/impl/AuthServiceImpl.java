package com.ly.cookbook.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ly.cookbook.common.units.ServletUtil;
import com.ly.cookbook.enums.LoginStatusEnum;
import com.ly.cookbook.enums.UserStatusEnum;
import com.ly.cookbook.exception.BaseException;
import com.ly.cookbook.exception.emun.ErrorCode;
import com.ly.cookbook.model.User;
import com.ly.cookbook.model.UserLoginLog;
import com.ly.cookbook.model.dto.LoginDTO;
import com.ly.cookbook.model.vo.LoginVO;
import com.ly.cookbook.model.vo.UserInfoVO;
import com.ly.cookbook.service.AuthService;
import com.ly.cookbook.service.UserLoginLogService;
import com.ly.cookbook.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 认证服务实现类
 *
 * @author admin
 * @date 2025-10-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserLoginLogService loginLogService;

    /**
     * 用户登录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 参数校验
        if (StringUtils.isBlank(loginDTO.getUsername())) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "用户名不能为空");
        }
        if (StringUtils.isBlank(loginDTO.getPassword())) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "密码不能为空");
        }

        // 2. 查询用户
        User user = userService.getOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, loginDTO.getUsername())
        );

        // 获取登录信息
        String loginIp = ServletUtil.getClientIP();
        String browser = ServletUtil.getBrowser();
        String os = ServletUtil.getOS();

        // 3. 验证用户是否存在
        if (user == null) {
            // 记录登录失败日志
            saveLoginLog(null, loginDTO.getUsername(), loginIp, browser, os,
                    LoginStatusEnum.FAILURE, "用户不存在");
            throw new BaseException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }

        // 4. 验证密码
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            // 记录登录失败日志
            saveLoginLog(user.getId(), user.getUsername(), loginIp, browser, os,
                    LoginStatusEnum.FAILURE, "密码错误");
            throw new BaseException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }

        // 5. 验证用户状态
        if (!UserStatusEnum.ENABLED.getCode().equals(user.getStatus())) {
            // 记录登录失败日志
            saveLoginLog(user.getId(), user.getUsername(), loginIp, browser, os,
                    LoginStatusEnum.FAILURE, "账号已被禁用");
            throw new BaseException(ErrorCode.FORBIDDEN_ERROR, "账号已被禁用，请联系管理员");
        }

        // 6. 执行登录，将用户ID存入Session
        // 如果选择记住我，设置7天有效期；否则使用默认配置
        if (Boolean.TRUE.equals(loginDTO.getRememberMe())) {
            StpUtil.login(user.getId(), 60 * 60 * 24 * 7); // 7天
        } else {
            StpUtil.login(user.getId());
        }

        // 8. 获取Token信息
        String token = StpUtil.getTokenValue();
        long tokenTimeout = StpUtil.getTokenTimeout();

        // 9. 更新用户最后登录时间
        user.setLastLoginTime(new Date());
        user.setEditTime(new Date());
        userService.updateById(user);

        // 10. 记录登录成功日志
        saveLoginLog(user.getId(), user.getUsername(), loginIp, browser, os,
                LoginStatusEnum.SUCCESS, "登录成功");

        // 11. 构建返回结果
        return LoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .token(token)
                .tokenTimeout(tokenTimeout)
                .avatarUrl(user.getAvatarUrl())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

    /**
     * 用户登出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout() {
        // 1. 检查是否已登录
        if (!StpUtil.isLogin()) {
            return;
        }

        // 2. 获取当前登录用户信息
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);

        // 3. 获取登录信息
        String loginIp = ServletUtil.getClientIP();
        String browser = ServletUtil.getBrowser();
        String os = ServletUtil.getOS();

        // 4. 执行登出
        StpUtil.logout();

        // 5. 记录登出日志
        if (user != null) {
            saveLoginLog(user.getId(), user.getUsername(), loginIp, browser, os,
                    LoginStatusEnum.LOGOUT, "登出成功");
            log.info("用户登出成功，用户名：{}，IP：{}", user.getUsername(), loginIp);
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @Override
    public UserInfoVO getCurrentUserInfo() {
        // 1. 检查是否已登录
        if (!StpUtil.isLogin()) {
            throw new BaseException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }

        // 2. 获取用户ID
        Long userId = StpUtil.getLoginIdAsLong();

        // 3. 查询用户信息
        User user = userService.getById(userId);
        if (user == null) {
            throw new BaseException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 4. 构建返回结果
        return UserInfoVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .lastLoginTime(user.getLastLoginTime())
                .createTime(user.getCreateTime())
                .build();
    }

    /**
     * 检查用户是否已登录
     */
    @Override
    public boolean isLogin() {
        return StpUtil.isLogin();
    }

    /**
     * 保存登录日志
     */
    private void saveLoginLog(Long userId, String username, String loginIp,
                              String browser, String os, LoginStatusEnum status, String message) {
        try {
            UserLoginLog loginLog = new UserLoginLog();
            loginLog.setUserId(userId);
            loginLog.setUsername(username);
            loginLog.setLoginTime(new Date());
            loginLog.setLoginIp(loginIp);
            loginLog.setBrowser(browser);
            loginLog.setOs(os);
            loginLog.setLoginStatus(status.getCode());
            loginLog.setLoginMessage(message);
            loginLog.setCreateTime(new Date());

            loginLogService.save(loginLog);

            log.info("登录日志记录成功，用户名：{}，状态：{}，IP：{}", username, status.getDesc(), loginIp);
        } catch (Exception e) {
            log.error("登录日志记录失败", e);
            // 日志记录失败不影响主流程
        }
    }
}

