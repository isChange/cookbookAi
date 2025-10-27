-- ==========================================
-- 表: spring_ai_chat_memory
-- 说明: Spring AI 聊天记忆表
-- ==========================================
CREATE TABLE spring_ai_chat_memory
(
    conversation_id VARCHAR(36)  NOT NULL,
    content         TEXT         NOT NULL,
    type            VARCHAR(10)  NOT NULL
        CONSTRAINT spring_ai_chat_memory_type_check
            CHECK ((type)::TEXT = ANY (
                (ARRAY['USER'::CHARACTER VARYING, 'ASSISTANT'::CHARACTER VARYING, 
                       'SYSTEM'::CHARACTER VARYING, 'TOOL'::CHARACTER VARYING])::TEXT[]
            )),
    timestamp       TIMESTAMP    NOT NULL
);

-- 表权限
ALTER TABLE spring_ai_chat_memory OWNER TO postgres;

-- 索引
CREATE INDEX spring_ai_chat_memory_conversation_id_timestamp_idx 
    ON spring_ai_chat_memory (conversation_id, timestamp);

-- ==========================================
-- 表: sys_user
-- 说明: 用户信息表
-- ==========================================
CREATE TABLE sys_user
(
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50)                                   NOT NULL UNIQUE,
    password        VARCHAR(255)                                  NOT NULL,
    nickname        VARCHAR(100),
    email           VARCHAR(100),
    phone           VARCHAR(20),
    role            VARCHAR(50) DEFAULT 'USER'::CHARACTER VARYING NOT NULL,
    status          SMALLINT    DEFAULT 1                         NOT NULL,
    avatar_url      VARCHAR(255),
    last_login_time TIMESTAMP,
    create_time     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP         NOT NULL,
    update_time     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP         NOT NULL,
    edit_time       TIMESTAMP
);

-- 表注释
COMMENT ON TABLE sys_user IS '用户信息表';

-- 列注释
COMMENT ON COLUMN sys_user.id IS '用户ID（主键）';
COMMENT ON COLUMN sys_user.username IS '用户账号（唯一）';
COMMENT ON COLUMN sys_user.password IS '用户密码（加密存储）';
COMMENT ON COLUMN sys_user.nickname IS '用户昵称';
COMMENT ON COLUMN sys_user.email IS '电子邮箱';
COMMENT ON COLUMN sys_user.phone IS '手机号码';
COMMENT ON COLUMN sys_user.role IS '用户角色：USER-普通用户，ADMIN-管理员，SUPER_ADMIN-超级管理员';
COMMENT ON COLUMN sys_user.status IS '状态：0-禁用，1-启用';
COMMENT ON COLUMN sys_user.avatar_url IS '头像地址';
COMMENT ON COLUMN sys_user.last_login_time IS '最后登录时间';
COMMENT ON COLUMN sys_user.create_time IS '创建时间';
COMMENT ON COLUMN sys_user.update_time IS '更新时间（数据库级别，任何修改都会自动更新）';
COMMENT ON COLUMN sys_user.edit_time IS '编辑时间（接口级别，通过接口修改时更新）。当edit_time=update_time时说明是通过接口修改，否则是直接操作数据库';

-- 表权限
ALTER TABLE sys_user OWNER TO postgres;

-- 索引
CREATE INDEX idx_sys_user_username ON sys_user (username);
CREATE INDEX idx_sys_user_role ON sys_user (role);
CREATE INDEX idx_sys_user_status ON sys_user (status);
CREATE INDEX idx_sys_user_create_time ON sys_user (create_time);

-- ==========================================
-- 表: user_login_log
-- 说明: 用户登录日志表
-- ==========================================
CREATE TABLE user_login_log
(
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT                              NOT NULL,
    username       VARCHAR(50)                         NOT NULL,
    login_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    login_ip       VARCHAR(50),
    login_location VARCHAR(100),
    browser        VARCHAR(100),
    os             VARCHAR(100),
    login_status   SMALLINT  DEFAULT 1                 NOT NULL,
    login_message  VARCHAR(255),
    create_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 表注释
COMMENT ON TABLE user_login_log IS '用户登录日志表';

-- 列注释
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

-- 表权限
ALTER TABLE user_login_log OWNER TO postgres;

-- 索引
CREATE INDEX idx_login_log_user_id ON user_login_log (user_id);
CREATE INDEX idx_login_log_username ON user_login_log (username);
CREATE INDEX idx_login_log_login_time ON user_login_log (login_time);
CREATE INDEX idx_login_log_login_status ON user_login_log (login_status);

