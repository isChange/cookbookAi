# RabbitMQ 消息队列使用文档

## 📦 项目结构

```
com.ly.cookbook.mq
├── config/
│   └── RabbitMQConfiguration.java      # RabbitMQ 配置类
├── dto/
│   └── CookbookMessage.java           # 消息实体类
├── producer/
│   └── MessageSender.java             # 消息发送工具类
└── consumer/
    └── MessageConsumer.java           # 消息消费者
```

## ⚙️ 配置说明

### 1. 配置文件（application-local.yml）

```yaml
spring:
  rabbitmq:
    host: localhost           # RabbitMQ 服务器地址
    port: 5672               # RabbitMQ 端口
    username: guest          # 用户名
    password: guest          # 密码
    virtual-host: /          # 虚拟主机
```

### 2. 交换机和队列配置

- **交换机名称**: `cookbook`
- **交换机类型**: `Topic`（支持路由键模糊匹配）
- **队列名称**: `cookbook`
- **路由键**: `cookbook.#`（匹配所有以 cookbook. 开头的路由）

## 🚀 使用方法

### 1. 发送消息

#### 方式一：使用快捷方法

```java
@Resource
private MessageSender messageSender;

// 发送简单消息
messageSender.sendSimple("user.register", userData);

// 发送带用户ID的消息
messageSender.sendWithUserId("chat.log", chatData, userId);
```

#### 方式二：使用 Builder 构建完整消息

```java
CookbookMessage message = CookbookMessage.builder()
    .messageId(UUID.randomUUID().toString())
    .messageType("custom.event")
    .data(yourData)
    .userId(userId)
    .createTime(LocalDateTime.now())
    .remark("自定义备注")
    .build();

messageSender.send(message);
```

#### 方式三：使用工厂方法

```java
// 不带用户ID
CookbookMessage message = CookbookMessage.create("test.message", "Hello MQ");

// 带用户ID
CookbookMessage message = CookbookMessage.create("test.message", "Hello MQ", 123L);

messageSender.send(message);
```

### 2. 消费消息

消费者已自动配置，收到消息后会根据 `messageType` 自动分发处理：

```java
// MessageConsumer.java 中添加新的消息类型处理
private void handleMessageByType(CookbookMessage message) {
    switch (message.getMessageType()) {
        case "your.message.type":
            handleYourMessage(message);
            break;
        // ...
    }
}

private void handleYourMessage(CookbookMessage message) {
    // 实现你的业务逻辑
    log.info("处理自定义消息: {}", message.getData());
}
```

## 📝 已支持的消息类型

| 消息类型 | 说明 | 数据格式 |
|---------|------|---------|
| `user.register` | 用户注册 | 用户对象 |
| `chat.log` | 聊天日志 | 聊天记录对象 |
| `token.update` | Token 更新 | Token 使用信息 |
| `test.message` | 测试消息 | 任意字符串 |

## 🧪 测试接口

访问 Swagger 文档测试 RabbitMQ 功能：

```
http://localhost:8000/api/v1/doc.html
```

找到 **"RabbitMQ 测试接口"** 分组，提供以下测试接口：

1. **发送简单测试消息** - `/mq/test/send-simple`
2. **发送带用户ID的消息** - `/mq/test/send-with-user`
3. **发送复杂对象消息** - `/mq/test/send-object`
4. **发送聊天日志消息** - `/mq/test/send-chat-log`
5. **发送 Token 更新消息** - `/mq/test/send-token-update`
6. **发送自定义消息** - `/mq/test/send-custom`
7. **批量发送消息** - `/mq/test/send-batch`

## 💡 实际应用场景

### 1. 异步处理聊天日志

在 `ChatTokenHandlerAdvisor` 中发送消息：

```java
@Component
public class ChatTokenHandlerAdvisor implements StreamAdvisor {
    @Resource
    private MessageSender messageSender;
    
    private void handlerToken(ChatClientResponse response, Long userId) {
        Usage usage = response.chatResponse().getMetadata().getUsage();
        
        // 发送 Token 更新消息到 MQ，异步处理
        Map<String, Object> data = new HashMap<>();
        data.put("consumedTokens", usage.getTotalTokens());
        data.put("timestamp", System.currentTimeMillis());
        
        messageSender.sendWithUserId("token.update", data, userId);
    }
}
```

### 2. 用户注册后发送欢迎消息

```java
@Service
public class UserService {
    @Resource
    private MessageSender messageSender;
    
    public void register(UserDTO userDTO) {
        // 保存用户
        User user = saveUser(userDTO);
        
        // 发送注册消息到 MQ，异步处理欢迎邮件等
        messageSender.sendWithUserId("user.register", user, user.getId());
    }
}
```

### 3. 记录聊天历史

```java
public void afterChatComplete(String question, String answer, Long userId) {
    Map<String, Object> chatLog = new HashMap<>();
    chatLog.put("question", question);
    chatLog.put("answer", answer);
    chatLog.put("timestamp", System.currentTimeMillis());
    
    messageSender.sendWithUserId("chat.log", chatLog, userId);
}
```

## ⚠️ 注意事项

### 1. 消息确认机制

当前配置使用**手动确认模式**（`acknowledge-mode: manual`）：

- ✅ **成功处理**: 调用 `channel.basicAck()` 确认消息
- ❌ **处理失败**: 调用 `channel.basicNack()` 拒绝消息并重新入队

### 2. 消息幂等性

使用 `messageId` 实现幂等性：

```java
// 消费者中添加去重逻辑
private Set<String> processedMessageIds = new ConcurrentHashSet<>();

if (processedMessageIds.contains(message.getMessageId())) {
    log.warn("重复消息，跳过处理: {}", message.getMessageId());
    channel.basicAck(deliveryTag, false);
    return;
}

// 处理消息...
processedMessageIds.add(message.getMessageId());
```

### 3. 延迟消息

`sendDelayed()` 方法使用 TTL 实现延迟，如需真正的延迟队列：

1. 安装 RabbitMQ 延迟插件：
   ```bash
   rabbitmq-plugins enable rabbitmq_delayed_message_exchange
   ```

2. 修改交换机类型为 `x-delayed-message`

### 4. 死信队列（可选）

如需处理失败的消息，可配置死信队列：

```java
@Bean
public Queue deadLetterQueue() {
    return QueueBuilder.durable("cookbook.dlq").build();
}

@Bean
public Queue cookbookQueue() {
    return QueueBuilder.durable(COOKBOOK_QUEUE)
        .withArgument("x-dead-letter-exchange", "cookbook.dlx")
        .withArgument("x-dead-letter-routing-key", "cookbook.dlq")
        .build();
}
```

## 🔍 监控和调试

### 查看 RabbitMQ 管理界面

```
http://localhost:15672
用户名: guest
密码: guest
```

### 日志级别配置

```yaml
logging:
  level:
    com.ly.cookbook.mq: DEBUG
    org.springframework.amqp: DEBUG
```

## 📚 扩展阅读

- [Spring AMQP 官方文档](https://docs.spring.io/spring-amqp/reference/)
- [RabbitMQ 官方文档](https://www.rabbitmq.com/documentation.html)
- [RabbitMQ 延迟插件](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange)

---

**最后更新**: 2025-10-20

