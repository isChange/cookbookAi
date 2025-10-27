-- ===========================
-- 审计功能测试脚本
-- ===========================
-- 此脚本用于测试 edit_time 和 update_time 的审计功能

-- 清理测试数据
DELETE FROM sys_user WHERE username = 'test_audit_user';

-- 1. 插入测试用户
INSERT INTO sys_user (username, password, nickname, role, status)
VALUES ('test_audit_user', '$2a$10$test', '审计测试用户', 'USER', 1);

-- 查看初始状态
SELECT id, username, nickname, create_time, update_time, edit_time,
       CASE 
           WHEN edit_time IS NULL THEN '从未通过接口修改'
           WHEN edit_time = update_time THEN '接口修改'
           ELSE '直接数据库修改'
       END as modify_source
FROM sys_user 
WHERE username = 'test_audit_user';

-- 预期结果：edit_time 为 NULL


-- 2. 模拟通过接口修改（同时设置 edit_time）
UPDATE sys_user 
SET nickname = '通过接口修改', 
    email = 'api@example.com',
    edit_time = CURRENT_TIMESTAMP
WHERE username = 'test_audit_user';

-- 等待1秒（确保时间戳可区分）
SELECT pg_sleep(1);

-- 查看接口修改后的状态
SELECT id, username, nickname, email, 
       update_time, edit_time,
       CASE 
           WHEN edit_time = update_time THEN '✅ 接口修改'
           WHEN edit_time != update_time THEN '❌ 直接数据库修改'
           ELSE '❌ 从未通过接口修改'
       END as modify_source,
       update_time - edit_time as time_diff
FROM sys_user 
WHERE username = 'test_audit_user';

-- 预期结果：edit_time = update_time（或差值在几毫秒内）


-- 3. 模拟直接在数据库修改（不设置 edit_time）
SELECT pg_sleep(1);

UPDATE sys_user 
SET nickname = '直接数据库修改', 
    phone = '13800138000'
WHERE username = 'test_audit_user';

-- 查看数据库修改后的状态
SELECT id, username, nickname, phone,
       update_time, edit_time,
       CASE 
           WHEN edit_time IS NULL THEN '❌ 从未通过接口修改'
           WHEN edit_time = update_time THEN '❌ 接口修改'
           ELSE '✅ 直接数据库修改'
       END as modify_source,
       update_time - edit_time as time_diff
FROM sys_user 
WHERE username = 'test_audit_user';

-- 预期结果：edit_time != update_time（update_time 更新了，edit_time 保持不变）


-- 4. 再次通过接口修改
SELECT pg_sleep(1);

UPDATE sys_user 
SET nickname = '再次接口修改', 
    edit_time = CURRENT_TIMESTAMP
WHERE username = 'test_audit_user';

-- 查看最终状态
SELECT id, username, nickname,
       update_time, edit_time,
       CASE 
           WHEN edit_time = update_time THEN '✅ 接口修改'
           WHEN edit_time != update_time THEN '❌ 直接数据库修改'
           ELSE '❌ 从未通过接口修改'
       END as modify_source,
       update_time - edit_time as time_diff
FROM sys_user 
WHERE username = 'test_audit_user';

-- 预期结果：edit_time = update_time


-- 5. 审计查询：查找所有异常修改
SELECT '=== 审计报告：查找直接在数据库修改的记录 ===' as report_title;

SELECT id, username, nickname, 
       update_time, 
       edit_time,
       CASE 
           WHEN edit_time IS NULL THEN '从未通过接口修改'
           WHEN edit_time != update_time THEN '直接数据库修改'
           ELSE '接口修改'
       END as modify_source,
       update_time - edit_time as time_diff
FROM sys_user
WHERE edit_time IS NULL OR edit_time != update_time
ORDER BY update_time DESC;


-- 6. 触发器验证：确认触发器存在
SELECT '=== 触发器检查 ===' as check_title;

SELECT trigger_name, event_manipulation, event_object_table, action_statement
FROM information_schema.triggers
WHERE event_object_table = 'sys_user'
  AND trigger_name = 'trigger_update_sys_user_update_time';


-- 7. 函数验证：确认函数存在
SELECT '=== 函数检查 ===' as check_title;

SELECT routine_name, routine_type, data_type
FROM information_schema.routines
WHERE routine_name = 'update_sys_user_update_time';


-- 清理测试数据（可选）
-- DELETE FROM sys_user WHERE username = 'test_audit_user';



