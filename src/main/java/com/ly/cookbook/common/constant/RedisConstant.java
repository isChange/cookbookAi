package com.ly.cookbook.common.constant;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description
 *  * Redis命名规范:
 *  * 1、和表结构相关     表名+":"+字段名+":"+字段值      例如: sys_user:age:123456789
 *  * 2、和业务相关      业务名+":"+业务字段+":"+业务值   例如: sys:id:1234
 *  * 3、接口相关       接口名+":"+接口字段+":"+字段值   例如: api_login:id:123456789
 * @createDate：2025/10/14 15:16
 * @email liuyia2022@163.com
 */
public class RedisConstant {

    /**
     * 用户注册凭证 Key 前缀
     * 完整格式: user:register:code:{注册码}
     * 说明: 由内部App用户生成的注册凭证，类似邀请码
     * 用户信息存储在 Value 中，通过 List 维护用户生成的注册码
     */
    public static final String USER_REGISTER_CODE = "user:register:code:";
    
    /**
     * 用户注册码列表 Key 前缀
     * 完整格式: user:register:code:list:{用户ID}
     * 说明: 使用 Redis List 维护用户生成的注册码列表
     * 不设置过期时间，采用懒删除策略，当列表为空时删除整个 Key
     */
    public static final String USER_REGISTER_CODE_LIST = "user:register:code:list:";
    
    /**
     * 注册码过期时间（秒）：1天 = 24小时 = 86400秒
     * 说明: 注册码的有效期，过期后自动失效
     */
    public static final long REGISTER_CODE_EXPIRE_TIME = 86400L;
}
