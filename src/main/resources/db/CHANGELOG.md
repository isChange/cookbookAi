# 数据库设计更新日志

## 2025-10-11 - 用户模块设计优化

### 主要变更

#### 1. 统一时间字段命名规范
将所有时间字段统一使用 `_time` 后缀命名：

| 旧字段名 | 新字段名 | 说明 |
|---------|---------|------|
| `created_at` | `create_time` | 创建时间 |
| `updated_at` | `update_time` | 更新时间 |
| `edited_at` | `edit_time` | 编辑时间 |

**影响范围**：
- `sys_user` 表
- `user_login_log` 表

#### 2. 添加自动更新触发器
创建了数据库触发器，确保 `update_time` 字段自动更新：

```sql
CREATE TRIGGER trigger_update_sys_user_update_time
BEFORE UPDATE ON sys_user
FOR EACH ROW
EXECUTE FUNCTION update_sys_user_update_time();
```

**功能说明**：
- ✅ 任何对 `sys_user` 表的修改都会自动更新 `update_time`
- ✅ 无论是通过接口还是直接在数据库修改，都能保证 `update_time` 的准确性
- ✅ 应用层无需手动设置 `update_time`

#### 3. 完善数据修改审计机制

通过对比 `edit_time` 和 `update_time` 来追踪数据修改来源：

| 场景 | edit_time | update_time | 判断结果 |
|------|-----------|------------|---------|
| 通过接口修改 | 2025-10-11 10:00:00 | 2025-10-11 10:00:00 | ✅ 接口修改（相等） |
| 直接数据库修改 | 2025-10-11 09:00:00 | 2025-10-11 10:00:00 | ⚠️ 数据库修改（不相等） |
| 从未接口修改 | NULL | 2025-10-11 10:00:00 | ⚠️ 数据库修改（NULL） |

### 使用指南

#### 应用层代码更新规范

**✅ 正确做法**：
```java
// 更新用户时，只需设置 edit_time
user.setNickname("新昵称");
user.setEditTime(new Timestamp(System.currentTimeMillis()));
// update_time 由触发器自动处理，无需手动设置
userMapper.updateById(user);
```

**❌ 错误做法**：
```java
// 不要忘记设置 edit_time
user.setNickname("新昵称");
// 缺少 edit_time 设置，会被判定为数据库直接修改
userMapper.updateById(user);
```

#### 审计查询示例

**查找可疑的数据库直接修改**：
```sql
SELECT id, username, nickname, 
       update_time, edit_time,
       CASE 
           WHEN edit_time IS NULL THEN '从未通过接口修改'
           WHEN edit_time != update_time THEN '直接数据库修改'
           ELSE '接口修改'
       END as modify_source
FROM sys_user
WHERE edit_time IS NULL OR edit_time != update_time
ORDER BY update_time DESC;
```

### 测试验证

提供了完整的测试脚本：`test-audit-feature.sql`

执行测试：
```bash
psql -U your_username -d your_database -f src/main/resources/db/test-audit-feature.sql
```

测试内容：
1. ✅ 插入新用户，验证初始状态
2. ✅ 模拟接口修改，验证 edit_time = update_time
3. ✅ 模拟数据库修改，验证 edit_time ≠ update_time
4. ✅ 审计查询，验证异常修改识别
5. ✅ 触发器检查，确认触发器正常工作

### 迁移步骤

如果已有旧表，请执行以下迁移：

```sql
-- 1. 重命名字段
ALTER TABLE sys_user 
    RENAME COLUMN created_at TO create_time;
    
ALTER TABLE sys_user 
    RENAME COLUMN updated_at TO update_time;
    
ALTER TABLE sys_user 
    RENAME COLUMN edited_at TO edit_time;

ALTER TABLE user_login_log 
    RENAME COLUMN created_at TO create_time;

-- 2. 重建索引
DROP INDEX IF EXISTS idx_sys_user_created_at;
CREATE INDEX idx_sys_user_create_time ON sys_user(create_time);

-- 3. 创建触发器
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

### 注意事项

1. ⚠️ **应用层代码需要同步更新**：所有涉及用户更新的代码都需要设置 `edit_time`
2. ⚠️ **实体类字段名需要同步**：Java 实体类中的字段名要对应更新
3. ⚠️ **MyBatis XML 需要更新**：Mapper XML 中的字段名也要同步修改
4. ✅ **向下兼容**：触发器确保即使应用层未更新，`update_time` 也能正常工作

### 相关文件

- `init-user.sql` - 完整的表结构初始化脚本
- `USER_MODULE_README.md` - 详细的设计文档
- `test-audit-feature.sql` - 审计功能测试脚本
- `CHANGELOG.md` - 本更新日志



