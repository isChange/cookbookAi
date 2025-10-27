# Chat Memory 数据库初始化指南

## 功能说明

本项目使用 Spring AI 的 JdbcChatMemoryRepository 来实现AI对话记忆功能，支持：
- 多会话管理（通过 conversationId 区分）
- 自动保存对话历史
- 上下文记忆（最近10条消息）
- PostgreSQL 数据库存储

## 数据库初始化

### 1. 执行 SQL 脚本

在 PostgreSQL 数据库中执行 `init-chat-memory.sql` 脚本：

```bash
psql -h 115.190.45.174 -p 5432 -U postgres -d ai_agent -f init-chat-memory.sql
```

或者使用数据库管理工具（如 DBeaver、pgAdmin）直接执行脚本。

### 2. 验证表结构

执行以下 SQL 验证表是否创建成功：

```sql
SELECT * FROM chat_memory LIMIT 1;
```

## 表结构说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | VARCHAR(255) | 消息唯一标识（主键） |
| conversation_id | VARCHAR(255) | 会话ID，用于区分不同的对话 |
| created_at | TIMESTAMP | 创建时间 |
| content | TEXT | 消息内容 |
| metadata | TEXT | 元数据（JSON格式） |
| message_type | VARCHAR(50) | 消息类型：USER/ASSISTANT/SYSTEM |

## API 使用示例

### 1. 普通聊天（无记忆）

```bash
curl -X POST "http://localhost:8000/api/v1/chat/simple?message=你好"
```

### 2. 记忆对话

```bash
# 第一次对话
curl -X POST "http://localhost:8000/api/v1/chat/memory?conversationId=user123&message=我叫张三"

# 第二次对话（AI会记住前面的内容）
curl -X POST "http://localhost:8000/api/v1/chat/memory?conversationId=user123&message=我叫什么名字？"
```

### 3. 清除会话记忆

```bash
# 清除特定会话
curl -X DELETE "http://localhost:8000/api/v1/chat/memory/user123"

# 清除所有会话
curl -X DELETE "http://localhost:8000/api/v1/chat/memory/all"
```

## 配置说明

### application-local.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://115.190.45.174:5432/ai_agent
    username: postgres
    password: eQ7aJ1zm3q
  ai:
    dashscope:
      api-key: sk-c1fb54b6bcd34381b851e99152c560d4
      chat:
        options:
          model: qwen-max
```

### 记忆窗口大小

在 `PgChatMemoryConfiguration.java` 中可以调整记忆窗口大小：

```java
ChatMemory chatMemory = new MessageWindowChatMemory(chatMemoryRepository, 10); // 保留最近10条消息
```

## 最佳实践

1. **conversationId 设计**
   - 使用用户ID作为conversationId，实现用户级别的对话记忆
   - 使用 `userId_sessionId` 格式，实现会话级别的对话记忆
   - 使用 UUID，实现临时对话记忆

2. **记忆清理**
   - 定期清理过期对话记录
   - 用户注销时清除对应的记忆
   - 提供用户主动清除记忆的接口

3. **性能优化**
   - 合理设置记忆窗口大小（建议 5-20 条）
   - 定期归档历史对话数据
   - 使用索引优化查询性能

## 故障排查

### 1. 表不存在错误

确保已执行初始化脚本：
```sql
\dt chat_memory
```

### 2. 连接数据库失败

检查配置文件中的数据库连接信息是否正确。

### 3. 记忆功能不生效

确保：
- ChatMemory Bean 已正确配置
- 使用的是带记忆的 ChatClient
- conversationId 参数正确传递


