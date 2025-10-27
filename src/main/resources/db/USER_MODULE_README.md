# 用户模块数据库设计文档

## 概述
用户模块包含两个核心表：用户信息表（sys_user）和用户登录日志表（user_login_log）。

## 表结构设计

### 1. sys_user（用户信息表）

#### 表说明
存储用户的基本信息、账号密码、角色权限等数据。

#### 字段说明

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|---------|------|------|
| id | BIGSERIAL | PRIMARY KEY | 用户ID，自增主键 |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户账号，唯一标识 |
| password | VARCHAR(255) | NOT NULL | 密码（BCrypt加密存储） |
| nickname | VARCHAR(100) | - | 用户昵称 |
| email | VARCHAR(100) | - | 电子邮箱 |
| phone | VARCHAR(20) | - | 手机号码 |
| role | VARCHAR(50) | NOT NULL, DEFAULT 'USER' | 用户角色 |
| status | SMALLINT | NOT NULL, DEFAULT 1 | 账号状态 |
| avatar_url | VARCHAR(255) | - | 头像URL |
| last_login_time | TIMESTAMP | - | 最后登录时间 |
| create_time | TIMESTAMP | NOT NULL, DEFAULT NOW | 创建时间 |
| update_time | TIMESTAMP | NOT NULL, DEFAULT NOW | 更新时间（数据库级别，自动更新） |
| edit_time | TIMESTAMP | - | 编辑时间（接口级别） |

#### 角色类型
- `ADMIN`: 管理员
- `FREE_USER`: 免费用户
- `VIP_USER`: VIP用户

#### 状态码
- `0`: 禁用
- `1`: 启用

#### 索引
- `idx_sys_user_username`: username字段索引（唯一）
- `idx_sys_user_role`: role字段索引
- `idx_sys_user_status`: status字段索引
- `idx_sys_user_create_time`: create_time字段索引

---

### 2. user_login_log（用户登录日志表）

#### 表说明
记录用户的登录历史，包括登录时间、IP地址、设备信息等。

#### 字段说明

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|---------|------|------|
| id | BIGSERIAL | PRIMARY KEY | 日志ID，自增主键 |
| user_id | BIGINT | NOT NULL | 用户ID（关联sys_user.id） |
| username | VARCHAR(50) | NOT NULL | 用户账号（冗余存储） |
| login_time | TIMESTAMP | NOT NULL, DEFAULT NOW | 登录时间 |
| login_ip | VARCHAR(50) | - | 登录IP地址 |
| login_location | VARCHAR(100) | - | 登录地点 |
| browser | VARCHAR(100) | - | 浏览器类型 |
| os | VARCHAR(100) | - | 操作系统 |
| login_status | SMALLINT | NOT NULL, DEFAULT 1 | 登录状态 |
| login_message | VARCHAR(255) | - | 登录消息 |
| create_time | TIMESTAMP | NOT NULL, DEFAULT NOW | 记录创建时间 |

#### 登录状态码
- `0`: 登录失败
- `1`: 登录成功

#### 索引
- `idx_login_log_user_id`: user_id字段索引
- `idx_login_log_username`: username字段索引
- `idx_login_log_login_time`: login_time字段索引
- `idx_login_log_login_status`: login_status字段索引

#### 外键关系（可选）
```sql
ALTER TABLE user_login_log ADD CONSTRAINT fk_login_log_user_id 
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE;
```
*注：默认未启用外键约束，可根据实际需求决定是否启用*

---

## 使用说明

### 1. 执行初始化SQL
```bash
psql -U your_username -d your_database -f src/main/resources/db/init-user.sql
```

### 2. 密码加密
用户密码必须使用BCrypt等安全算法加密后存储，建议使用：
- Java: Spring Security的`BCryptPasswordEncoder`
- 加密强度: 10轮以上

示例（Java）：
```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String encodedPassword = encoder.encode("plainPassword");
```

### 3. 默认账号
初始化SQL会创建一个默认管理员账号：
- 用户名: `admin`
- 密码: `admin123`（需要修改SQL中的加密密码）
- 角色: `SUPER_ADMIN`

**重要提醒**：生产环境部署前务必修改默认密码！

### 4. 常用SQL示例

#### 创建新用户
```sql
INSERT INTO sys_user (username, password, nickname, email, role, status)
VALUES ('newuser', '$2a$10$...', '新用户', 'user@example.com', 'USER', 1);
```

#### 记录登录日志
```sql
INSERT INTO user_login_log (user_id, username, login_time, login_ip, browser, os, login_status, login_message)
VALUES (1, 'admin', NOW(), '192.168.1.100', 'Chrome 120', 'Windows 10', 1, '登录成功');
```

#### 查询用户登录历史
```sql
SELECT * FROM user_login_log 
WHERE user_id = 1 
ORDER BY login_time DESC 
LIMIT 10;
```

#### 统计用户登录次数
```sql
SELECT user_id, username, COUNT(*) as login_count
FROM user_login_log
WHERE login_status = 1
GROUP BY user_id, username
ORDER BY login_count DESC;
```

#### 更新最后登录时间
```sql
-- update_time 会自动通过触发器更新，无需手动设置
UPDATE sys_user 
SET last_login_time = NOW()
WHERE id = 1;
```

---

## 数据修改来源审计

### edit_time 字段说明

`edit_time` 字段用于追踪数据的修改来源，实现数据修改审计功能。

#### 审计逻辑
- **edit_time = update_time**：数据通过接口修改
- **edit_time ≠ update_time** 或 **edit_time 为 NULL**：数据直接在数据库修改

#### 自动更新机制
`update_time` 字段通过数据库触发器自动更新，无论是通过接口还是直接在数据库修改，`update_time` 都会自动更新为当前时间。

#### 使用方法

**1. 通过接口修改用户数据**
```sql
-- 应用层在更新数据时，必须同时设置 edit_time
-- update_time 会自动通过触发器更新
UPDATE sys_user 
SET nickname = '新昵称', 
    email = 'newemail@example.com',
    edit_time = NOW()
WHERE id = 1;
```

**2. 直接在数据库修改**
```sql
-- 不设置 edit_time，只有 update_time 自动更新
UPDATE sys_user 
SET nickname = '直接修改的昵称'
WHERE id = 1;
-- 此时 edit_time 不变，update_time 通过触发器自动更新
```

**3. 查询通过接口修改的记录**
```sql
SELECT id, username, nickname, update_time, edit_time
FROM sys_user
WHERE edit_time = update_time;
```

**4. 查询直接在数据库修改的记录**
```sql
SELECT id, username, nickname, update_time, edit_time
FROM sys_user
WHERE edit_time IS NULL OR edit_time != update_time;
```

**5. 审计报告：查找异常修改**
```sql
-- 查找最近被直接在数据库修改的用户
SELECT id, username, nickname, 
       update_time, 
       edit_time,
       CASE 
           WHEN edit_time IS NULL THEN '从未通过接口修改'
           WHEN edit_time != update_time THEN '直接数据库修改'
           ELSE '接口修改'
       END as modify_source
FROM sys_user
WHERE update_time > NOW() - INTERVAL '7 days'
  AND (edit_time IS NULL OR edit_time != update_time)
ORDER BY update_time DESC;
```

#### Java 应用层实现示例

```java
// 在 Service 层更新用户信息时
public void updateUser(Long userId, UserDTO userDTO) {
    SysUser user = new SysUser();
    user.setId(userId);
    user.setNickname(userDTO.getNickname());
    user.setEmail(userDTO.getEmail());
    
    // 关键：设置 edit_time，update_time 由触发器自动更新
    Timestamp now = new Timestamp(System.currentTimeMillis());
    user.setEditTime(now);
    // 不需要手动设置 updateTime，触发器会自动处理
    
    userMapper.updateById(user);
}
```

#### 最佳实践

1. ✅ **应用层更新必须设置 edit_time**：确保所有通过接口的更新都设置此字段
2. ✅ **数据库触发器自动更新 update_time**：无需手动设置，触发器确保任何修改都会更新此字段
3. ✅ **定期审计异常修改**：建立定期检查机制，发现异常的数据库直接修改
4. ✅ **权限控制**：严格控制数据库直接访问权限，减少绕过接口的修改

---

## 扩展功能

### 1. 自动更新update_time字段（已实现）
触发器已在初始化SQL中创建，会自动更新`update_time`字段：

```sql
-- 已包含在 init-user.sql 中
CREATE OR REPLACE FUNCTION update_sys_user_update_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_sys_user_update_time
BEFORE UPDATE ON sys_user
FOR EACH ROW
EXECUTE FUNCTION update_sys_user_update_time();
```

**工作原理**：
- 任何对 `sys_user` 表的 UPDATE 操作都会触发此触发器
- 触发器会自动将 `update_time` 设置为当前时间
- 无需在应用层手动设置 `update_time`

### 2. 定期清理历史日志
SQL中已包含清理函数`clean_old_login_logs()`，可通过以下方式调用：

```sql
-- 手动清理90天前的日志
SELECT clean_old_login_logs();
```

如需自动定期清理，可以：
- 使用PostgreSQL的pg_cron扩展
- 在应用层使用Spring的@Scheduled注解实现定时任务

### 3. 审计功能扩展
如需更完整的审计功能，可考虑添加：
- 登出时间记录
- 操作日志表
- 密码修改历史表
- 登录失败次数限制

---

## 性能优化建议

1. **分区表**：当登录日志数据量大时，可按时间分区
   ```sql
   CREATE TABLE user_login_log (...)
   PARTITION BY RANGE (login_time);
   ```

2. **归档历史数据**：定期将历史数据归档到历史表

3. **索引优化**：根据实际查询场景调整索引策略

4. **连接池配置**：合理配置数据库连接池参数

---

## 安全建议

1. ✅ 密码必须使用BCrypt等强加密算法
2. ✅ 限制登录失败次数，实现账号锁定机制
3. ✅ 记录详细的登录日志供审计使用
4. ✅ 定期检查异常登录行为
5. ✅ 生产环境禁止使用默认密码
6. ✅ 敏感字段（如密码）不可通过日志输出
7. ✅ 实施IP白名单或黑名单机制

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2025-10-11 | 初始版本 |


