-- PostgreSQL User Module Tables
-- 用户模块相关表

-- ===========================
-- 用户信息表
-- ===========================
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(50) NOT NULL DEFAULT 'FREE_USER',
    status SMALLINT NOT NULL DEFAULT 1,
    avatar_url VARCHAR(255),
    last_login_time TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edit_time TIMESTAMP
);

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_sys_user_username ON sys_user(username);
CREATE INDEX IF NOT EXISTS idx_sys_user_role ON sys_user(role);
CREATE INDEX IF NOT EXISTS idx_sys_user_status ON sys_user(status);
CREATE INDEX IF NOT EXISTS idx_sys_user_create_time ON sys_user(create_time);

-- 添加注释
COMMENT ON TABLE sys_user IS '用户信息表';
COMMENT ON COLUMN sys_user.id IS '用户ID（主键）';
COMMENT ON COLUMN sys_user.username IS '用户账号（唯一）';
COMMENT ON COLUMN sys_user.password IS '用户密码（加密存储）';
COMMENT ON COLUMN sys_user.nickname IS '用户昵称';
COMMENT ON COLUMN sys_user.email IS '电子邮箱';
COMMENT ON COLUMN sys_user.phone IS '手机号码';
COMMENT ON COLUMN sys_user.role IS '用户角色：ADMIN-管理员，FREE_USER-免费用户，VIP_USER-VIP用户';
COMMENT ON COLUMN sys_user.status IS '状态：0-禁用，1-启用';
COMMENT ON COLUMN sys_user.avatar_url IS '头像地址';
COMMENT ON COLUMN sys_user.last_login_time IS '最后登录时间';
COMMENT ON COLUMN sys_user.create_time IS '创建时间';
COMMENT ON COLUMN sys_user.update_time IS '更新时间（数据库级别，任何修改都会自动更新）';
COMMENT ON COLUMN sys_user.edit_time IS '编辑时间（接口级别，通过接口修改时更新）。当edit_time=update_time时说明是通过接口修改，否则是直接操作数据库';


-- ===========================
-- 用户登录日志表
-- ===========================
CREATE TABLE IF NOT EXISTS user_login_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL,
    login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    login_ip VARCHAR(50),
    login_location VARCHAR(100),
    browser VARCHAR(100),
    os VARCHAR(100),
    login_status SMALLINT NOT NULL DEFAULT 1,
    login_message VARCHAR(255),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_login_log_user_id ON user_login_log(user_id);
CREATE INDEX IF NOT EXISTS idx_login_log_username ON user_login_log(username);
CREATE INDEX IF NOT EXISTS idx_login_log_login_time ON user_login_log(login_time);
CREATE INDEX IF NOT EXISTS idx_login_log_login_status ON user_login_log(login_status);

-- 添加外键约束（可选，根据实际需求决定是否启用）
-- ALTER TABLE user_login_log ADD CONSTRAINT fk_login_log_user_id 
--     FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE;

-- 添加注释
COMMENT ON TABLE user_login_log IS '用户登录日志表';
COMMENT ON COLUMN user_login_log.id IS '日志ID（主键）';
COMMENT ON COLUMN user_login_log.user_id IS '用户ID';
COMMENT ON COLUMN user_login_log.username IS '用户账号（冗余存储）';
COMMENT ON COLUMN user_login_log.login_time IS '登录时间';
COMMENT ON COLUMN user_login_log.login_ip IS '登录IP地址';
COMMENT ON COLUMN user_login_log.login_location IS '登录地点';
COMMENT ON COLUMN user_login_log.browser IS '浏览器类型';
COMMENT ON COLUMN user_login_log.os IS '操作系统';
COMMENT ON COLUMN user_login_log.login_status IS '登录状态：0-失败，1-成功';
COMMENT ON COLUMN user_login_log.login_message IS '登录消息（成功或失败原因）';
COMMENT ON COLUMN user_login_log.create_time IS '记录创建时间';


-- ===========================
-- 初始化数据
-- ===========================
-- 插入默认管理员账号（密码需要使用BCrypt等加密后再插入）
-- 示例：密码 'admin123' 使用BCrypt加密后的值
INSERT INTO sys_user (username, password, nickname, role, status) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'ADMIN', 1)
ON CONFLICT (username) DO NOTHING;

-- 插入测试用户（可选）
INSERT INTO sys_user (username, password, nickname, role, status) 
VALUES ('user001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '测试用户', 'FREE_USER', 1)
ON CONFLICT (username) DO NOTHING;


-- ===========================
-- 定时清理历史日志（可选）
-- ===========================
-- 创建函数：清理90天前的登录日志
CREATE OR REPLACE FUNCTION clean_old_login_logs()
RETURNS void AS $$
BEGIN
    DELETE FROM user_login_log 
    WHERE login_time < NOW() - INTERVAL '90 days';
END;
$$ LANGUAGE plpgsql;

-- 如果需要定期自动清理，可以配置PostgreSQL的定时任务（pg_cron扩展）
-- 或在应用层实现定时清理


-- ===========================
-- 自动更新 update_time 触发器
-- ===========================
-- 创建触发器函数：自动更新 update_time 字段
CREATE OR REPLACE FUNCTION update_sys_user_update_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 创建触发器：在 sys_user 表更新时自动触发
CREATE TRIGGER trigger_update_sys_user_update_time
BEFORE UPDATE ON sys_user
FOR EACH ROW
EXECUTE FUNCTION update_sys_user_update_time();

COMMENT ON FUNCTION update_sys_user_update_time() IS '自动更新sys_user表的update_time字段';


-- ===========================
-- 数据修改来源审计说明
-- ===========================
-- edit_time 字段用于区分数据修改来源：
-- 1. 当 edit_time = update_time 时：说明数据是通过接口修改的
-- 2. 当 edit_time != update_time 或 edit_time IS NULL 时：说明数据是直接在数据库修改的
--
-- 使用示例：
-- 
-- 通过接口修改用户（应用层代码应该同时更新这两个字段）：
-- UPDATE sys_user 
-- SET nickname = '新昵称', edit_time = NOW(), update_time = NOW()
-- WHERE id = 1;
--
-- 查询直接在数据库修改的记录（审计用）：
-- SELECT id, username, nickname, update_time, edit_time
-- FROM sys_user
-- WHERE edit_time IS NULL OR edit_time != update_time;


