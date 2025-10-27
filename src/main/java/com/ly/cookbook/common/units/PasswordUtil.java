package com.ly.cookbook.common.units;

import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.crypto.digest.DigestUtil;

import java.nio.charset.StandardCharsets;

/**
 * 密码加密工具类
 *
 * @author 刘燚
 * @version v1.0.0
 * @Description 密码加密工具，支持 MD5 和 BCrypt 加密
 * @createDate 2025/5/31 17:20
 * @email liuyia2022@163.com
 */
public class PasswordUtil {

    /**
     * MD5 加密（带盐值）
     *
     * @param password 原始密码
     * @param salt     盐值
     * @return 加密后的密码
     * @deprecated 不推荐使用，建议使用 BCrypt
     */
    @Deprecated
    public static String encryptMd5(String password, String salt) {
        String text = salt + password + salt;
        return DigestUtil.md5Hex(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * MD5 加密（不带盐值）
     *
     * @param password 原始密码
     * @return 加密后的密码
     * @deprecated 不推荐使用，建议使用 BCrypt
     */
    @Deprecated
    public static String encryptMd5(String password) {
        return DigestUtil.md5Hex(password.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * BCrypt 加密（推荐）
     * BCrypt 是一种更安全的密码加密方式，每次加密结果都不同，且自带盐值
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encryptBCrypt(String password) {
        return BCrypt.hashpw(password);
    }

    /**
     * BCrypt 密码验证
     *
     * @param password 原始密码
     * @param hashed   加密后的密码
     * @return true-验证通过，false-验证失败
     */
    public static boolean checkBCrypt(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }

    /**
     * 生成测试密码（用于测试环境）
     * 默认密码：admin123
     *
     * @return BCrypt 加密后的 "admin123"
     */
    public static String generateTestPassword() {
        return encryptBCrypt("admin123");
    }
}
