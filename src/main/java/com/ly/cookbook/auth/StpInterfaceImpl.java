package com.ly.cookbook.auth;

import cn.dev33.satoken.stp.StpInterface;
import com.ly.cookbook.model.User;
import com.ly.cookbook.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description 返回当前登录用户的权限列表和角色列表
 * @createDate：2025/10/11 22:57
 * @email liuyia2022@163.com
 */
@Component
public class StpInterfaceImpl implements StpInterface {
    @Resource
    private UserService userService;
    @Override
    public List<String> getPermissionList(Object userId, String s) {
        return List.of();
    }

    @Override
    public List<String> getRoleList(Object userId, String s) {
        // 将userId转换为Long类型，避免类型不匹配导致PostgreSQL报错
        Long userIdLong = Long.valueOf(userId.toString());
        User user = userService.lambdaQuery().eq(User::getId, userIdLong).one();
        if (user != null){
            return List.of(user.getRole());
        }
        return List.of();
    }
}
