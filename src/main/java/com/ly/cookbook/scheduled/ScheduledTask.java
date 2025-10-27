package com.ly.cookbook.scheduled;

import com.ly.cookbook.common.constant.UserConstant;
import com.ly.cookbook.model.User;
import com.ly.cookbook.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/10/20 22:47
 * @email liuyia2022@163.com
 */
@Component
public class ScheduledTask {
    @Resource
    UserService userService;
    /**
     * 每小时刷新用户token
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void reflushTokenTaskByFree(){
        userService.lambdaUpdate().eq(User::getRole, UserConstant.USER_FREE).set(User::getUsedToken, 0L).update();
    }
}
