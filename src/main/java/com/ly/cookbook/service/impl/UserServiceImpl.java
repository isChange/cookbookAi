package com.ly.cookbook.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.cookbook.common.units.AssertUtil;
import com.ly.cookbook.common.units.PasswordUtil;
import com.ly.cookbook.enums.UserRoleEnum;
import com.ly.cookbook.enums.UserStatusEnum;
import com.ly.cookbook.exception.BaseException;
import com.ly.cookbook.exception.emun.ErrorCode;
import com.ly.cookbook.mapper.UserMapper;
import com.ly.cookbook.model.User;
import com.ly.cookbook.model.dto.ChangePasswordDTO;
import com.ly.cookbook.model.dto.RegisterDTO;
import com.ly.cookbook.model.dto.UpdateUserDTO;
import com.ly.cookbook.model.vo.TokenInfoVO;
import com.ly.cookbook.model.vo.UserInfoVO;
import com.ly.cookbook.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;

/**
* @author admin
* @description 针对表【sys_user(用户信息表)】的数据库操作Service实现
* @createDate 2025-10-11 18:34:50
*/
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    /**
     * 用户注册
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO register(RegisterDTO registerDTO) {
        // 1. 参数校验
        validateRegisterParams(registerDTO);

        // 2. 检查用户名是否已存在
        if (checkUsernameExists(registerDTO.getUsername())) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "用户名已存在");
        }

        // 3. 检查邮箱是否已被使用（如果提供了邮箱）
        if (StringUtils.isNotBlank(registerDTO.getEmail())) {
            User existingUser = this.getOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getEmail, registerDTO.getEmail())
            );
            if (existingUser != null) {
                throw new BaseException(ErrorCode.PARAMS_ERROR, "该邮箱已被注册");
            }
        }

        // 4. 检查手机号是否已被使用（如果提供了手机号）
        if (StringUtils.isNotBlank(registerDTO.getPhone())) {
            User existingUser = this.getOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getPhone, registerDTO.getPhone())
            );
            if (existingUser != null) {
                throw new BaseException(ErrorCode.PARAMS_ERROR, "该手机号已被注册");
            }
        }

        // 5. 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(PasswordUtil.encryptBCrypt(registerDTO.getPassword()));
        user.setNickname(StringUtils.isBlank(registerDTO.getNickname()) 
                ? registerDTO.getUsername() 
                : registerDTO.getNickname());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setRole(UserRoleEnum.FREE_USER.getCode()); // 默认为免费用户
        user.setStatus(UserStatusEnum.ENABLED.getCode()); // 默认启用
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        // 6. 保存到数据库
        boolean saved = this.save(user);
        if (!saved) {
            throw new BaseException(ErrorCode.SYSTEM_ERROR, "注册失败，请稍后重试");
        }

        log.info("用户注册成功，用户名：{}，ID：{}", user.getUsername(), user.getId());

        // 7. 返回用户信息
        return convertToUserInfoVO(user);
    }

    /**
     * 更新用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO updateUserInfo(Long userId, UpdateUserDTO updateUserDTO) {
        // 1. 查询用户
        User user = this.getById(userId);
        if (user == null) {
            throw new BaseException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 2. 检查邮箱是否被其他用户使用
        if (StringUtils.isNotBlank(updateUserDTO.getEmail()) 
                && !updateUserDTO.getEmail().equals(user.getEmail())) {
            User existingUser = this.getOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getEmail, updateUserDTO.getEmail())
                            .ne(User::getId, userId)
            );
            if (existingUser != null) {
                throw new BaseException(ErrorCode.PARAMS_ERROR, "该邮箱已被其他用户使用");
            }
        }

        // 3. 检查手机号是否被其他用户使用
        if (StringUtils.isNotBlank(updateUserDTO.getPhone()) 
                && !updateUserDTO.getPhone().equals(user.getPhone())) {
            User existingUser = this.getOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getPhone, updateUserDTO.getPhone())
                            .ne(User::getId, userId)
            );
            if (existingUser != null) {
                throw new BaseException(ErrorCode.PARAMS_ERROR, "该手机号已被其他用户使用");
            }
        }

        // 4. 更新用户信息
        if (StringUtils.isNotBlank(updateUserDTO.getNickname())) {
            user.setNickname(updateUserDTO.getNickname());
        }
        if (StringUtils.isNotBlank(updateUserDTO.getEmail())) {
            user.setEmail(updateUserDTO.getEmail());
        }
        if (StringUtils.isNotBlank(updateUserDTO.getPhone())) {
            user.setPhone(updateUserDTO.getPhone());
        }
        if (StringUtils.isNotBlank(updateUserDTO.getAvatarUrl())) {
            user.setAvatarUrl(updateUserDTO.getAvatarUrl());
        }

        // 设置编辑时间（标记为接口修改）
        user.setEditTime(new Date());

        // 5. 保存更新
        boolean updated = this.updateById(user);
        if (!updated) {
            throw new BaseException(ErrorCode.SYSTEM_ERROR, "更新失败，请稍后重试");
        }

        log.info("用户信息更新成功，用户ID：{}，用户名：{}", userId, user.getUsername());

        // 6. 返回更新后的用户信息
        return convertToUserInfoVO(user);
    }

    /**
     * 修改密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        // 1. 参数校验
        if (StringUtils.isBlank(changePasswordDTO.getOldPassword())) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "原密码不能为空");
        }
        if (StringUtils.isBlank(changePasswordDTO.getNewPassword())) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "新密码不能为空");
        }
        if (changePasswordDTO.getNewPassword().length() < 6) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "新密码长度不能少于6位");
        }
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "两次输入的新密码不一致");
        }
        if (changePasswordDTO.getOldPassword().equals(changePasswordDTO.getNewPassword())) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "新密码不能与原密码相同");
        }

        // 2. 查询用户
        User user = this.getById(userId);
        if (user == null) {
            throw new BaseException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 3. 验证原密码
        if (!BCrypt.checkpw(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "原密码错误");
        }

        // 4. 更新密码
        user.setPassword(PasswordUtil.encryptBCrypt(changePasswordDTO.getNewPassword()));
        user.setEditTime(new Date());

        boolean updated = this.updateById(user);
        if (!updated) {
            throw new BaseException(ErrorCode.SYSTEM_ERROR, "密码修改失败，请稍后重试");
        }

        log.info("用户密码修改成功，用户ID：{}，用户名：{}", userId, user.getUsername());
    }

    /**
     * 检查用户名是否存在
     */
    @Override
    public boolean checkUsernameExists(String username) {
        if (StringUtils.isBlank(username)) {
            return false;
        }
        Long count = this.count(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );
        return count > 0;
    }

    /**
     * 校验Token是否有效
     * @param userId 用户ID
     * @return
     */
    @Override
    public boolean checkUserToken(Long userId) {
        User user = this.getById(userId);
        AssertUtil.isNotNull(user, ErrorCode.NO_ACCOUNT);
        return user.getUsedToken() < user.getTotalToken();
    }

    @Override
    public TokenInfoVO getUserTokenInfo(Long userId) {
        User user = this.getById(userId);
        return TokenInfoVO.builder()
                .userId(userId)
                .role(user.getRole())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .usedToken(user.getUsedToken())
                .totalToken(user.getTotalToken())
                .build();
    }

    /**
     * 校验注册参数
     */
    private void validateRegisterParams(RegisterDTO registerDTO) {
        if (StringUtils.isBlank(registerDTO.getUsername())) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "用户名不能为空");
        }
        if (registerDTO.getUsername().length() < 4 || registerDTO.getUsername().length() > 20) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "用户名长度必须在4-20位之间");
        }
        if (!registerDTO.getUsername().matches("^[a-zA-Z0-9_]+$")) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "用户名只能包含字母、数字和下划线");
        }
        if (StringUtils.isBlank(registerDTO.getPassword())) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "密码不能为空");
        }
        if (registerDTO.getPassword().length() < 6) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "密码长度不能少于6位");
        }
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BaseException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 邮箱格式校验（如果提供了邮箱）
        if (StringUtils.isNotBlank(registerDTO.getEmail())) {
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
            if (!registerDTO.getEmail().matches(emailRegex)) {
                throw new BaseException(ErrorCode.PARAMS_ERROR, "邮箱格式不正确");
            }
        }
        // 手机号格式校验（如果提供了手机号）
        if (StringUtils.isNotBlank(registerDTO.getPhone())) {
            String phoneRegex = "^1[3-9]\\d{9}$";
            if (!registerDTO.getPhone().matches(phoneRegex)) {
                throw new BaseException(ErrorCode.PARAMS_ERROR, "手机号格式不正确");
            }
        }
    }

    /**
     * 转换为 UserInfoVO
     */
    private UserInfoVO convertToUserInfoVO(User user) {
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
}




