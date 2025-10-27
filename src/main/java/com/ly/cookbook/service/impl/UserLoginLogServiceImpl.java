package com.ly.cookbook.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.cookbook.model.UserLoginLog;
import com.ly.cookbook.service.UserLoginLogService;
import com.ly.cookbook.mapper.UserLoginLogMapper;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【user_login_log(用户登录日志表)】的数据库操作Service实现
* @createDate 2025-10-11 18:40:07
*/
@Service
public class UserLoginLogServiceImpl extends ServiceImpl<UserLoginLogMapper, UserLoginLog>
    implements UserLoginLogService{

}




