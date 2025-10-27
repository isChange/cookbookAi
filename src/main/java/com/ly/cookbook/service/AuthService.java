package com.ly.cookbook.service;

import com.ly.cookbook.model.dto.LoginDTO;
import com.ly.cookbook.model.vo.LoginVO;
import com.ly.cookbook.model.vo.UserInfoVO;

/**
 * 认证服务接口
 *
 * @author admin
 * @date 2025-10-11
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录响应
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    UserInfoVO getCurrentUserInfo();

    /**
     * 检查用户是否已登录
     *
     * @return true-已登录，false-未登录
     */
    boolean isLogin();
}

