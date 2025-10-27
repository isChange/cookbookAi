package com.ly.cookbook.service;

import java.util.List;
import java.util.Map;

/**
 * 注册码服务接口
 * 说明: 注册码是由内部App用户生成的注册凭证（类似邀请码）
 * 外部用户必须持有有效的注册码才能完成注册
 *
 * @author admin
 * @date 2025-10-14
 */
public interface RegisterCodeService {

    /**
     * 生成注册码（由内部App用户调用）
     * 生成唯一的注册码，有效期为 1 天
     * 
     * @param generatorUserId 生成者用户ID（当前登录的App用户）
     * @param remark 备注说明（可选，记录这个注册码的用途）
     * @return 生成的注册码
     */
    String generateRegisterCode(Long generatorUserId, String remark);

    /**
     * 验证注册码是否有效（用于用户注册时验证）
     * 验证成功后直接删除注册码，确保一次性使用
     * 同时从用户的注册码列表中移除
     *
     * @param code 注册码
     * @return true-验证成功，false-验证失败（不存在或已过期）
     */
    boolean validateAndRemoveCode(String code);

    /**
     * 验证注册码是否有效（不删除）
     * 用于预检查注册码有效性
     *
     * @param code 注册码
     * @return true-有效，false-无效
     */
    boolean validateCode(String code);

    /**
     * 删除指定的注册码（由生成者或管理员调用）
     *
     * @param code 注册码
     * @return true-删除成功，false-删除失败
     */
    boolean removeCode(String code);

    /**
     * 获取注册码剩余有效时间（秒）
     *
     * @param code 注册码
     * @return 剩余有效时间（秒），如果不存在返回 -1
     */
    long getCodeExpireTime(String code);

    /**
     * 批量生成注册码
     *
     * @param generatorUserId 生成者用户ID
     * @param count 生成数量
     * @param remark 备注说明
     * @return 生成的注册码列表
     */
    List<String> batchGenerateRegisterCodes(Long generatorUserId, int count, String remark);

    /**
     * 查询用户生成的注册码列表
     *
     * @param generatorUserId 生成者用户ID
     * @return 注册码列表
     */
    List<String> getGeneratorCodes(Long generatorUserId);

    /**
     * 查询注册码详细信息
     * 包括生成者、是否已使用、使用时间等
     *
     * @param code 注册码
     * @return 注册码详细信息，不存在返回 null
     */
    Map<String, Object> getCodeInfo(String code);

    /**
     * 查询注册码详细信息（已知用户ID）
     * 性能更好，无需遍历
     *
     * @param generatorUserId 生成者用户ID
     * @param code 注册码
     * @return 注册码详细信息，不存在返回 null
     */
    Map<String, Object> getCodeInfo(Long generatorUserId, String code);
}

