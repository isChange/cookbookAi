package com.ly.cookbook.service;

import com.ly.cookbook.model.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Service;

/**
 * Token 统计服务
 * 用于统计和更新用户的 Token 使用量
 *
 * @author admin
 * @date 2025-10-20
 */

public interface TokenStatisticsService {
    /**
     * 更新用户 Token 使用量
     *
     * @param userId 用户ID
     * @param usage  Token 使用情况
     */
    void updateUserToken(Long userId, Usage usage);

    /**
     * 更新用户 Token 使用量（根据 Token 数量）
     *
     * @param userId 用户ID
     * @param tokens 消耗的 Token 数量
     */
    void updateUserToken(Long userId, long tokens);

}