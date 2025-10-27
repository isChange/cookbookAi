package com.ly.cookbook.service.impl;

import cn.hutool.core.lang.UUID;
import com.ly.cookbook.cache.RedisCache;
import com.ly.cookbook.common.constant.RedisConstant;
import com.ly.cookbook.common.units.AssertUtil;
import com.ly.cookbook.exception.emun.RegisterCodeErrorEnum;
import com.ly.cookbook.service.RegisterCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 注册码服务实现类
 * 说明: 注册码是由内部App用户生成的注册凭证（类似邀请码）
 * Redis Key 格式: user:register:code:{用户ID}:{注册码}
 *
 * @author admin
 * @date 2025-10-14
 */
@Slf4j
@Service
public class RegisterCodeServiceImpl implements RegisterCodeService {

    /**
     * 注册码前缀（便于识别）
     */
    private static final String CODE_PREFIX = "REG";

    /**
     * 生成注册码（由内部App用户调用）
     * 生成格式: REG-{UUID前12位}，例如: REG-A1B2C3D4E5F6
     * Redis Key 格式: user:register:code:{注册码}
     * 同时维护 List: user:register:code:list:{用户ID}
     *
     * @param generatorUserId 生成者用户ID
     * @param remark 备注说明
     * @return 生成的注册码
     */
    @Override
    public String generateRegisterCode(Long generatorUserId, String remark) {
        // 参数校验
        AssertUtil.isNotNull(generatorUserId, RegisterCodeErrorEnum.GENERATOR_USER_ID_EMPTY);

        // 生成唯一注册码: REG-{UUID前12位}
        String uuid = UUID.randomUUID().toString(true).toUpperCase();
        String code = CODE_PREFIX + "-" + uuid.substring(0, 12);

        // 构建 Redis Key: user:register:code:{注册码}
        String redisKey = RedisConstant.USER_REGISTER_CODE + code;

        // 构建注册码信息
        Map<String, Object> codeInfo = new HashMap<>();
        codeInfo.put("code", code);
        codeInfo.put("generatorUserId", generatorUserId);
        codeInfo.put("remark", remark);
        codeInfo.put("generateTime", System.currentTimeMillis());

        // 存储到 Redis，设置过期时间为 1 天
        RedisCache.setObject(
                redisKey,
                codeInfo,
                (int) RedisConstant.REGISTER_CODE_EXPIRE_TIME,
                TimeUnit.SECONDS
        );

        // 同时添加到用户的注册码列表中（List 不设置过期时间）
        String listKey = RedisConstant.USER_REGISTER_CODE_LIST + generatorUserId;
        RedisCache.cache.opsForList().rightPush(listKey, code);

        log.info("生成注册码成功，注册码：{}，生成者：{}，备注：{}", code, generatorUserId, remark);

        return code;
    }

    /**
     * 验证注册码是否有效（用于用户注册时验证）
     * 验证成功后直接删除注册码，确保一次性使用
     * 同时从用户的注册码列表中移除
     * 验证失败抛出异常
     *
     * @param code 注册码
     * @throws com.ly.cookbook.exception.AssertException 验证失败时抛出
     */
    @Override
    public boolean validateAndRemoveCode(String code) {
        // 参数校验
        AssertUtil.isNotBlank(code, RegisterCodeErrorEnum.REGISTER_CODE_EMPTY);

        // 直接构建 Redis Key: user:register:code:{注册码}
        String redisKey = RedisConstant.USER_REGISTER_CODE + code;
        
        // 从 Redis 获取注册码信息
        Map<String, Object> codeInfo = RedisCache.getObject(redisKey);

        // 注册码不存在或已过期，抛出异常
        AssertUtil.isNotNull(codeInfo, RegisterCodeErrorEnum.REGISTER_CODE_NOT_FOUND);
        AssertUtil.isFalse(codeInfo.isEmpty(), RegisterCodeErrorEnum.REGISTER_CODE_NOT_FOUND);

        // 提取生成者用户ID
        Object userIdObj = codeInfo.get("generatorUserId");
        Long userId = null;
        if (userIdObj instanceof Long) {
            userId = (Long) userIdObj;
        } else if (userIdObj instanceof Integer) {
            userId = ((Integer) userIdObj).longValue();
        }

        // 验证成功，直接删除注册码
        Boolean removed = RedisCache.removeObject(redisKey);
        log.info("验证注册码成功，注册码：{}，已删除：{}", code, removed);

        // 同时从用户的注册码列表中移除
        if (userId != null) {
            String listKey = RedisConstant.USER_REGISTER_CODE_LIST + userId;
            Long removeCount = RedisCache.cache.opsForList().remove(listKey, 1, code);
            
            // 检查列表是否为空，如果为空则删除 List Key
            Long listSize = RedisCache.cache.opsForList().size(listKey);
            if (listSize != null && listSize == 0) {
                RedisCache.cache.delete(listKey);
                log.info("注册码列表已空，删除 List Key，用户：{}", userId);
            }
            
            log.debug("从用户列表中移除注册码，用户：{}，注册码：{}，移除数量：{}", userId, code, removeCount);
        }

        return true;
    }

    /**
     * 验证注册码是否有效（不删除）
     * 使用简化的 Key 设计，直接定位，性能优秀 O(1)
     * 验证失败抛出异常
     *
     * @param code 注册码
     * @return true-有效
     * @throws com.ly.cookbook.exception.AssertException 验证失败时抛出
     */
    @Override
    public boolean validateCode(String code) {
        // 参数校验
        AssertUtil.isNotBlank(code, RegisterCodeErrorEnum.REGISTER_CODE_EMPTY);

        // 直接构建 Redis Key: user:register:code:{注册码}
        String redisKey = RedisConstant.USER_REGISTER_CODE + code;
        
        // 从 Redis 获取注册码信息
        Map<String, Object> codeInfo = RedisCache.getObject(redisKey);

        // 注册码不存在或已过期，抛出异常
        AssertUtil.isNotNull(codeInfo, RegisterCodeErrorEnum.REGISTER_CODE_NOT_FOUND);
        AssertUtil.isFalse(codeInfo.isEmpty(), RegisterCodeErrorEnum.REGISTER_CODE_NOT_FOUND);

        log.info("验证注册码（不删除）：注册码：{}，有效", code);
        return true;
    }

    /**
     * 删除指定的注册码
     * 同时从用户的注册码列表中移除
     * 删除失败抛出异常
     *
     * @param code 注册码
     * @return true-删除成功
     * @throws com.ly.cookbook.exception.AssertException 删除失败时抛出
     */
    @Override
    public boolean removeCode(String code) {
        // 参数校验
        AssertUtil.isNotBlank(code, RegisterCodeErrorEnum.REGISTER_CODE_EMPTY);

        // 直接构建 Redis Key: user:register:code:{注册码}
        String redisKey = RedisConstant.USER_REGISTER_CODE + code;
        
        // 获取注册码信息（用于提取生成者用户ID）
        Map<String, Object> codeInfo = RedisCache.getObject(redisKey);
        
        // 注册码不存在，抛出异常
        AssertUtil.isNotNull(codeInfo, RegisterCodeErrorEnum.REGISTER_CODE_NOT_EXIST);
        AssertUtil.isFalse(codeInfo.isEmpty(), RegisterCodeErrorEnum.REGISTER_CODE_NOT_EXIST);
        
        // 提取生成者用户ID
        Object userIdObj = codeInfo.get("generatorUserId");
        if (userIdObj != null) {
            Long userId = null;
            if (userIdObj instanceof Long) {
                userId = (Long) userIdObj;
            } else if (userIdObj instanceof Integer) {
                userId = ((Integer) userIdObj).longValue();
            }
            
            if (userId != null) {
                // 从用户的注册码列表中移除
                String listKey = RedisConstant.USER_REGISTER_CODE_LIST + userId;
                Long removeCount = RedisCache.cache.opsForList().remove(listKey, 1, code);
                
                // 检查列表是否为空，如果为空则删除 List Key
                Long listSize = RedisCache.cache.opsForList().size(listKey);
                if (listSize != null && listSize == 0) {
                    RedisCache.cache.delete(listKey);
                    log.info("注册码列表已空，删除 List Key，用户：{}", userId);
                }
                
                log.debug("从用户列表中移除注册码，用户：{}，注册码：{}，移除数量：{}", userId, code, removeCount);
            }
        }
        
        // 删除 Redis 中的注册码主 Key
        Boolean removed = RedisCache.removeObject(redisKey);
        log.info("删除注册码：注册码：{}，删除结果：{}", code, removed);

        return true;
    }

    /**
     * 获取注册码剩余有效时间（秒）
     * 使用简化的 Key 设计，直接定位，性能优秀 O(1)
     * 查询失败抛出异常
     *
     * @param code 注册码
     * @return 剩余有效时间（秒）
     * @throws com.ly.cookbook.exception.AssertException 查询失败时抛出
     */
    @Override
    public long getCodeExpireTime(String code) {
        // 参数校验
        AssertUtil.isNotBlank(code, RegisterCodeErrorEnum.REGISTER_CODE_EMPTY);

        // 直接构建 Redis Key: user:register:code:{注册码}
        String redisKey = RedisConstant.USER_REGISTER_CODE + code;
        
        // 获取剩余过期时间
        Long expireTime = RedisCache.cache.getExpire(redisKey, TimeUnit.SECONDS);

        // 注册码不存在或已过期，抛出异常
        AssertUtil.isNotNull(expireTime, RegisterCodeErrorEnum.REGISTER_CODE_NOT_FOUND);
        AssertUtil.isTrue(expireTime >= 0, RegisterCodeErrorEnum.REGISTER_CODE_NOT_FOUND);

        log.debug("查询注册码剩余时间：注册码：{}，剩余：{}秒", code, expireTime);
        return expireTime;
    }

    /**
     * 批量生成注册码
     *
     * @param generatorUserId 生成者用户ID
     * @param count 生成数量
     * @param remark 备注说明
     * @return 生成的注册码列表
     * @throws com.ly.cookbook.exception.AssertException 参数校验失败时抛出
     */
    @Override
    public List<String> batchGenerateRegisterCodes(Long generatorUserId, int count, String remark) {
        // 参数校验
        AssertUtil.isNotNull(generatorUserId, RegisterCodeErrorEnum.GENERATOR_USER_ID_EMPTY);
        AssertUtil.isTrue(count > 0 && count <= 100, RegisterCodeErrorEnum.BATCH_COUNT_INVALID);

        List<String> codes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String code = generateRegisterCode(generatorUserId, remark);
            codes.add(code);
        }

        log.info("批量生成注册码成功，生成者：{}，数量：{}，备注：{}", generatorUserId, count, remark);
        return codes;
    }

    /**
     * 查询用户生成的注册码列表
     * 使用 Redis List 维护，采用懒删除策略
     * 1. 从 List 中读取所有注册码
     * 2. 检查每个注册码是否过期或已被删除
     * 3. 过期/删除的注册码从 List 中移除
     * 4. 如果 List 为空，删除整个 List 的 Key
     *
     * @param generatorUserId 生成者用户ID
     * @return 注册码列表（仅包含有效的注册码）
     * @throws com.ly.cookbook.exception.AssertException 参数校验失败时抛出
     */
    @Override
    public List<String> getGeneratorCodes(Long generatorUserId) {
        // 参数校验
        AssertUtil.isNotNull(generatorUserId, RegisterCodeErrorEnum.USER_ID_EMPTY);

        // 构建 List Key
        String listKey = RedisConstant.USER_REGISTER_CODE_LIST + generatorUserId;
        
        // 从 Redis List 中获取所有注册码
        List<Object> rawCodes = RedisCache.cache.opsForList().range(listKey, 0, -1);
        
        if (rawCodes == null || rawCodes.isEmpty()) {
            log.info("查询用户生成的注册码列表，用户：{}，列表为空", generatorUserId);
            return new ArrayList<>();
        }

        List<String> validCodes = new ArrayList<>();
        List<String> expiredCodes = new ArrayList<>();
        
        // 遍历检查每个注册码的有效性（懒删除）
        for (Object obj : rawCodes) {
            if (obj == null) {
                continue;
            }
            
            String code = obj.toString();
            
            // 构建注册码主 Key: user:register:code:{code}
            String codeKey = RedisConstant.USER_REGISTER_CODE + code;
            
            // 检查注册码是否仍然存在（未过期）
            Map<String, Object> codeInfo = RedisCache.getObject(codeKey);
            
            if (codeInfo != null && !codeInfo.isEmpty()) {
                // 注册码有效，添加到结果列表
                validCodes.add(code);
            } else {
                // 注册码已过期或被删除，标记为待删除
                expiredCodes.add(code);
                log.debug("发现过期注册码，将从列表中删除，用户：{}，注册码：{}", generatorUserId, code);
            }
        }
        
        // 从 List 中删除过期的注册码
        if (!expiredCodes.isEmpty()) {
            for (String expiredCode : expiredCodes) {
                RedisCache.cache.opsForList().remove(listKey, 1, expiredCode);
            }
            log.info("清理过期注册码，用户：{}，清理数量：{}", generatorUserId, expiredCodes.size());
        }
        
        // 如果列表为空，删除整个 List Key
        if (validCodes.isEmpty()) {
            Boolean deleted = RedisCache.cache.delete(listKey);
            log.info("注册码列表已空，删除 List Key，用户：{}，删除结果：{}", generatorUserId, deleted);
        }
        
        log.info("查询用户生成的注册码列表，用户：{}，有效数量：{}，过期数量：{}", 
                generatorUserId, validCodes.size(), expiredCodes.size());

        return validCodes;
    }

    /**
     * 查询注册码详细信息
     * 使用简化的 Key 设计，直接定位，性能优秀 O(1)
     * 查询失败抛出异常
     *
     * @param code 注册码
     * @return 注册码详细信息
     * @throws com.ly.cookbook.exception.AssertException 查询失败时抛出
     */
    @Override
    public Map<String, Object> getCodeInfo(String code) {
        // 参数校验
        AssertUtil.isNotBlank(code, RegisterCodeErrorEnum.REGISTER_CODE_EMPTY);

        // 直接构建 Redis Key: user:register:code:{注册码}
        String redisKey = RedisConstant.USER_REGISTER_CODE + code;
        
        // 从 Redis 获取注册码信息
        Map<String, Object> codeInfo = RedisCache.getObject(redisKey);

        // 注册码不存在，抛出异常
        AssertUtil.isNotNull(codeInfo, RegisterCodeErrorEnum.REGISTER_CODE_NOT_FOUND);
        AssertUtil.isFalse(codeInfo.isEmpty(), RegisterCodeErrorEnum.REGISTER_CODE_NOT_FOUND);

        log.debug("查询注册码详细信息，注册码：{}", code);
        return codeInfo;
    }

    /**
     * 查询注册码详细信息（已知用户ID）
     * 保留此方法以保持接口兼容性
     *
     * @param generatorUserId 生成者用户ID
     * @param code 注册码
     * @return 注册码详细信息
     */
    @Override
    public Map<String, Object> getCodeInfo(Long generatorUserId, String code) {
        // 用户ID不影响查询，直接调用简化版本
        return getCodeInfo(code);
    }
}
