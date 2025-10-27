package com.ly.cookbook.service.impl;

import com.ly.cookbook.model.User;
import com.ly.cookbook.service.TokenStatisticsService;
import com.ly.cookbook.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Service;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/10/21 11:05
 * @email liuyia2022@163.com
 */
@Service
@Slf4j
public class TokenStatisticsServiceImpl implements TokenStatisticsService {
    @Resource
    private UserService userService;

    /**
     * 更新用户 Token 使用量
     *
     * @param userId 用户ID
     * @param usage  Token 使用情况
     */
    public void updateUserToken(Long userId, Usage usage) {
        if (userId == null || usage == null) {
            log.warn("userId 或 usage 为空，跳过 Token 统计");
            return;
        }

        try {
            User user = userService.getById(userId);
            if (user != null) {
                long consumedTokens = usage.getTotalTokens().longValue();
                long newUsedToken = user.getUsedToken() + consumedTokens;

                userService.lambdaUpdate()
                        .eq(User::getId, userId)
                        .set(User::getUsedToken, newUsedToken)
                        .update();

                log.info("用户 Token 更新成功! userId: {}, 消费: {}, 总计: {}",
                        userId, consumedTokens, newUsedToken);
            } else {
                log.warn("用户不存在，userId: {}", userId);
            }
        } catch (Exception e) {
            log.error("更新用户 Token 失败, userId: {}, error: {}", userId, e.getMessage(), e);
            throw new RuntimeException("更新用户 Token 失败", e);
        }
    }

    /**
     * 更新用户 Token 使用量（根据 Token 数量）
     *
     * @param userId 用户ID
     * @param tokens 消耗的 Token 数量
     */
    public void updateUserToken(Long userId, long tokens) {
        if (userId == null || tokens <= 0) {
            log.warn("userId 为空或 tokens 无效，跳过 Token 统计");
            return;
        }

        try {
            User user = userService.getById(userId);
            if (user != null) {
                long newUsedToken = user.getUsedToken() + tokens;

                userService.lambdaUpdate()
                        .eq(User::getId, userId)
                        .set(User::getUsedToken, newUsedToken)
                        .update();

                log.info("用户 Token 更新成功! userId: {}, 消费: {}, 总计: {}",
                        userId, tokens, newUsedToken);
            } else {
                log.warn("用户不存在，userId: {}", userId);
            }
        } catch (Exception e) {
            log.error("更新用户 Token 失败, userId: {}, error: {}", userId, e.getMessage(), e);
            throw new RuntimeException("更新用户 Token 失败", e);
        }
    }
}
