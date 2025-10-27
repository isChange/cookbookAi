-- ===========================
-- 插入测试用户数据
-- ===========================
-- 注意：此脚本仅用于开发和测试环境，生产环境请勿使用！

-- 密码说明：
-- 所有测试用户的密码都是：admin123
-- BCrypt 加密后的结果（每次运行会生成不同的哈希值，但都能验证 admin123）

-- 清理已存在的测试用户（可选）
-- DELETE FROM sys_user WHERE username IN ('admin', 'user001', 'user002', 'testuser');

-- 插入管理员
INSERT INTO sys_user (username, password, nickname, email, phone, role, status, create_time, update_time)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 
        'admin@example.com', '13800138000', 'ADMIN', 1, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- 插入免费用户1
INSERT INTO sys_user (username, password, nickname, email, phone, role, status, create_time, update_time)
VALUES ('freeuser1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '免费用户1', 
        'freeuser1@example.com', '13800138001', 'FREE_USER', 1, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- 插入免费用户2
INSERT INTO sys_user (username, password, nickname, email, phone, role, status, create_time, update_time)
VALUES ('freeuser2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '免费用户2', 
        'freeuser2@example.com', '13800138002', 'FREE_USER', 1, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- 插入VIP用户1
INSERT INTO sys_user (username, password, nickname, email, phone, role, status, create_time, update_time)
VALUES ('vipuser1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'VIP用户1', 
        'vipuser1@example.com', '13800138003', 'VIP_USER', 1, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- 插入VIP用户2
INSERT INTO sys_user (username, password, nickname, email, phone, role, status, create_time, update_time)
VALUES ('vipuser2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'VIP用户2', 
        'vipuser2@example.com', '13800138004', 'VIP_USER', 1, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- 插入禁用用户（用于测试账号禁用场景）
INSERT INTO sys_user (username, password, nickname, email, phone, role, status, create_time, update_time)
VALUES ('disabled_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '禁用用户', 
        'disabled@example.com', '13800138005', 'FREE_USER', 0, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- 查看插入结果
SELECT id, username, nickname, role, status, create_time 
FROM sys_user 
WHERE username IN ('admin', 'freeuser1', 'freeuser2', 'vipuser1', 'vipuser2', 'disabled_user')
ORDER BY id;

-- ===========================
-- 测试账号信息汇总
-- ===========================
-- 
-- | 用户名        | 密码      | 角色       | 状态   | 说明           |
-- |--------------|-----------|-----------|--------|----------------|
-- | admin        | admin123  | ADMIN     | 启用   | 管理员         |
-- | freeuser1    | admin123  | FREE_USER | 启用   | 免费用户1      |
-- | freeuser2    | admin123  | FREE_USER | 启用   | 免费用户2      |
-- | vipuser1     | admin123  | VIP_USER  | 启用   | VIP用户1       |
-- | vipuser2     | admin123  | VIP_USER  | 启用   | VIP用户2       |
-- | disabled_user| admin123  | FREE_USER | 禁用   | 禁用用户（测试）|
--


