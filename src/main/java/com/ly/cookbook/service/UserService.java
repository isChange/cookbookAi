package com.ly.cookbook.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.cookbook.model.User;
import com.ly.cookbook.model.dto.ChangePasswordDTO;
import com.ly.cookbook.model.dto.RegisterDTO;
import com.ly.cookbook.model.dto.UpdateUserDTO;
import com.ly.cookbook.model.vo.TokenInfoVO;
import com.ly.cookbook.model.vo.UserInfoVO;

/**
* @author admin
* @description 针对表【sys_user(用户信息表)】的数据库操作Service
* @createDate 2025-10-11 18:34:50
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 用户信息
     */
    UserInfoVO register(RegisterDTO registerDTO);

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param updateUserDTO 更新信息
     * @return 用户信息
     */
    UserInfoVO updateUserInfo(Long userId, UpdateUserDTO updateUserDTO);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param changePasswordDTO 修改密码信息
     */
    void changePassword(Long userId, ChangePasswordDTO changePasswordDTO);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return true-存在，false-不存在
     */
    boolean checkUsernameExists(String username);

    /**
     * 检查用户是否有可用Token
     * @param userId 用户ID
     * @return
     */
    boolean checkUserToken(Long userId);

    /**
     * 返回用户Token信息
     * @param userId 用户ID
     * @return
     */
    TokenInfoVO getUserTokenInfo(Long userId);
}
