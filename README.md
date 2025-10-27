<div align="center">

# 🍳 Cookbook - AI智能烹饪助手

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-blue.svg)](https://docs.spring.io/spring-ai/reference/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**基于 Spring AI 构建的智能烹饪助手系统**

采用 AI Agent 架构 · 集成阿里云通义千问大模型 · 提供智能化菜谱查询与烹饪指导

[快速开始](#-快速开始) · [在线文档](http://localhost:8000/api/v1/doc.html) · [报告问题](https://github.com/your-username/cookbook-app/issues)

</div>

---

## 📋 目录

<table>
<tr>
<td>

### 📚 基础
- [🎯 项目简介](#-项目简介)
- [🏗 技术架构](#-技术架构)
- [🚀 核心功能](#-核心功能)
- [🎨 系统架构](#-系统架构)

</td>
<td>

### 🛠 开发
- [⚡ 快速开始](#-快速开始)
- [🔧 配置说明](#-配置说明)
- [📁 项目结构](#-项目结构)
- [💻 开发指南](#-开发指南)

</td>
<td>

### 📦 部署与优化
- [🐳 Docker 部署](#-docker-部署)
- [📊 性能优化](#-性能优化)
- [🔒 安全说明](#-安全说明)
- [🧪 测试](#-测试)

</td>
</tr>
</table>

---

## 🎯 项目简介

**Cookbook** 是一个现代化的 AI 智能助手系统，专注于烹饪领域的知识服务。项目采用 **Agent 架构**，通过集成多种 AI 工具（网页搜索、文件操作、终端操作等），实现了从简单菜谱查询到复杂烹饪技巧指导的全方位智能服务。

### 🌟 核心特性

<table>
<tr>
<td width="50%">

#### 🤖 AI Agent 智能体
基于 ReAct 和 ToolCall 架构，实现智能任务规划与执行

#### 💬 流式对话
支持 SSE（Server-Sent Events）实时流式响应

#### 🧠 记忆系统
基于 PostgreSQL 的持久化聊天记忆，支持上下文连续对话

#### 🔧 多工具集成
网页搜索、文件操作、资源下载、终端操作等多种工具

</td>
<td width="50%">

#### 🔐 完整权限系统
基于 Sa-Token 的用户认证与授权

#### 📊 监控告警
Prometheus + Actuator 实时监控系统运行状态

#### 📝 全面文档
Knife4j 自动生成的交互式 API 文档

#### 🐳 容器化支持
完整的 Docker 和 Docker Compose 部署方案

</td>
</tr>
</table>

---

## 🏗 技术架构

### 核心技术栈

| 技术领域 | 技术选型 | 版本 | 说明 |
|:---------|:---------|:-----|:-----|
| **核心框架** | Spring Boot | 3.5.6 | 应用基础框架 |
| **编程语言** | Java | 21 | JDK版本 |
| **AI 框架** | Spring AI | 1.0.0 | Spring 官方 AI 框架 |
| **AI 提供商** | Spring AI Alibaba | 1.0.0.2 | 阿里云 DashScope 集成 |
| **大模型** | 通义千问 | qwen-max | 阿里云大语言模型 |
| **数据库** | PostgreSQL | - | 关系型数据库 |
| **缓存** | Redis | - | 分布式缓存 |
| **ORM 框架** | MyBatis Plus | 3.5.14 | 持久层框架 |
| **安全认证** | Sa-Token | 1.44.0 | 权限认证框架 |
| **API 文档** | Knife4j | 4.4.0 | OpenAPI 3.0 文档 |
| **监控** | Prometheus + Actuator | - | 应用监控 |
| **工具库** | Hutool | 5.8.37 | Java工具类库 |
| **HTML 解析** | Jsoup | 1.18.1 | 网页内容解析 |
| **容器化** | Docker | - | 容器化部署 |

### 系统架构图

```
flowchart TD
    %% 客户端层
    subgraph A[客户端层]
        A1[Web前端]
        A2[移动端]
        A3[API调用方]
    end

    %% API 网关层
    subgraph B[API 网关层]
        direction TB
        B1[Agent控制器]
        B2[Chat控制器]
        B3[Auth控制器]
        B4[User控制器]
        B5[Health控制器]
    end

    %% 业务逻辑层
    subgraph C[业务逻辑层]
        subgraph C1[AI Agent 智能体核心]
            C1_1[YiCookAgent<br/>烹饪智能助手]
            
            subgraph C1_2[Agent 类型]
                C1_2_1[ReAct Agent<br/>推理与行动]
                C1_2_2[ToolCall Agent<br/>工具调用]
            end
        end

        subgraph C2[Advisor 增强层]
            C2_1[ChatLogAdvisor<br/>日志记录]
            C2_2[RagCloudAdvisor<br/>RAG检索增强]
            C2_3[ChatTokenHandlerAdvisor<br/>Token管理]
        end

        subgraph C3[AI Tools 工具集]
            subgraph C3_1[搜索与网页工具]
                C3_1_1[WebSearch<br/>网页搜索]
                C3_1_2[WebScript<br/>网页脚本]
            end
            
            subgraph C3_2[文件与资源工具]
                C3_2_1[FileTools<br/>文件操作]
                C3_2_2[Resource Download<br/>资源下载]
            end
            
            subgraph C3_3[系统工具]
                C3_3_1[Terminal Operation<br/>终端操作]
                C3_3_2[Terminate<br/>终止工具]
            end
        end

        subgraph C4[Service 业务服务层]
            C4_1[UserService<br/>用户服务]
            C4_2[ChatService<br/>聊天服务]
            C4_3[AuthService<br/>认证服务]
        end
    end

    %% 数据持久层
    subgraph D[数据持久层]
        D1[PostgreSQL<br/>业务数据]
        D2[Redis Cache<br/>会话缓存]
        D3[Chat Memory (JDBC)<br/>聊天记忆]
    end

    %% 外部服务层
    subgraph E[外部服务层]
        E1[阿里云 DashScope]
        E2[SerpAPI<br/>网页搜索]
        E3[RabbitMQ<br/>消息队列]
    end

    %% 连接关系
    A -->|HTTP/HTTPS + SSE| B
    
    B --> C1
    B --> C4
    
    C1_1 --> C1_2
    C1_1 --> C2
    C1_1 --> C3
    
    C2 --> C4_2
    C3 --> E2
    C3 --> E3
    
    C4 --> D
    C4 --> E3
    
    D --> C4
    E1 --> C1

    %% 样式设置
    style A fill:#e1f5fe
    style B fill:#f3e5f5
    style C fill:#e8f5e8
    style D fill:#fff3e0
    style E fill:#fce4ec
    
    style C1 fill:#c8e6c9
    style C2 fill:#b3e5fc
    style C3 fill:#fff9c4
    style C4 fill:#ffecb3
    
    style A1 fill:#bbdefb
    style A2 fill:#bbdefb
    style A3 fill:#bbdefb
    
    style B1 fill:#e1bee7
    style B2 fill:#e1bee7
    style B3 fill:#e1bee7
    style B4 fill:#e1bee7
    style B5 fill:#e1bee7
```

---

## 🚀 核心功能

### 1️⃣ AI Agent 智能体系统

#### YiCookAgent - 烹饪智能助手

基于 **Spring AI** 框架实现的智能 Agent，具备以下能力：

- **自主任务规划**：根据用户需求自动分解任务，选择合适的工具组合
- **上下文理解**：基于 ChatMemory 的持久化记忆，支持多轮连续对话
- **工具调用**：智能调用 6+ 工具完成复杂任务
- **实时流式响应**：支持 SSE 流式输出，实时反馈执行过程

**核心能力：**

```text
📌 Agent 能力矩阵
├─ 菜谱查询与生成
├─ 烹饪技巧指导
├─ 食材替代建议
├─ 网页信息检索
├─ 文件内容处理
└─ 资源下载管理
```

#### Agent 架构类型

| Agent类型 | 说明 | 应用场景 |
|:----------|:-----|:---------|
| **ReActAgent** | Reasoning + Acting 模式 | 需要推理和行动结合的复杂任务 |
| **ToolCallAgent** | 工具调用模式 | 需要调用外部工具的任务 |
| **BaseAgent** | 基础抽象类 | 提供通用 Agent 能力 |

### 2️⃣ AI 工具集（AI Tools）

| 工具名称 | 功能说明 |
|:---------|:---------|
| 🔍 **WebSearchTools** | • 集成 SerpAPI 实现 Google 搜索<br>• 实时获取最新菜谱和烹饪资讯<br>• 支持关键词智能提取 |
| 📁 **FileTools** | • 文件读写操作<br>• 文件格式转换<br>• 文件内容分析 |
| 📥 **ResourceDownloadTools** | • 网络资源下载<br>• 图片、视频等多媒体资源获取<br>• 下载进度管理 |
| 💻 **TerminalOperationTools** | • 系统命令执行<br>• 脚本运行支持 |
| 🌐 **WebScriptTools** | • 网页内容爬取<br>• HTML 解析（Jsoup）<br>• 结构化数据提取 |
| ⛔ **TerminateTools** | • Agent 任务终止<br>• 异常处理 |

### 3️⃣ 用户认证与权限系统

**基于 Sa-Token 实现的完整权限体系：**

| 功能 | 说明 |
|:-----|:-----|
| ✅ 用户注册与登录 | 完整的用户认证流程 |
| ✅ JWT Token 认证 | 无状态 Token 认证 |
| ✅ 角色权限管理 | USER / ADMIN / SUPER_ADMIN |
| ✅ 用户状态管理 | 启用/禁用用户账号 |
| ✅ 登录日志记录 | 完整的登录审计 |
| ✅ 多端登录控制 | 单端/多端登录策略 |

**数据模型：**

| 表名 | 说明 |
|:-----|:-----|
| `sys_user` | 用户基本信息 + 角色 + 状态 |
| `user_login_log` | IP地址 + 浏览器 + 操作系统 + 登录状态 |

### 4️⃣ 聊天记忆系统

基于 **Spring AI Chat Memory** 实现：

| 特性 | 说明 |
|:-----|:-----|
| 💾 持久化存储 | 基于 PostgreSQL 数据库 |
| 🔄 自动上下文管理 | 智能加载历史对话 |
| 📝 消息类型支持 | USER / ASSISTANT / SYSTEM / TOOL |
| 🕐 时间序列优化 | 索引优化查询性能 |

### 5️⃣ 监控与可观测性

**基于 Prometheus + Actuator：**

| 功能 | 说明 |
|:-----|:-----|
| 📊 实时指标监控 | 监控应用运行状态 |
| 🔔 性能告警 | 异常情况自动告警 |
| 📈 业务数据统计 | 统计业务关键指标 |

**监控端点：**

| 端点 | 说明 |
|:-----|:-----|
| `/actuator/health` | 健康检查 |
| `/actuator/metrics` | 指标数据 |
| `/actuator/prometheus` | Prometheus 格式数据 |

### 6️⃣ 异步消息处理

**基于 RabbitMQ 实现：**

| 特性 | 说明 |
|:-----|:-----|
| 📮 异步任务处理 | 解耦业务逻辑 |
| 🔁 消息重试机制 | 失败自动重试 |
| ✅ 手动消息确认 | 保证消息可靠性 |
| 📊 消息追踪 | 完整的消息链路追踪 |

---

## 🎨 系统架构

### Agent 执行流程

```
┌──────────────────────────────────────────────────────────┐
│                      用户输入                             │
│         "教我做红烧肉，要有详细步骤"                      │
└─────────────────┬────────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────────────┐
│            1. Agent 接收请求                            │
│   - 加载会话上下文（ChatMemory）                         │
│   - 初始化工具集（Tools）                                │
│   - 设置系统提示词（System Prompt）                      │
└─────────────────┬───────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────────────┐
│            2. Advisor 链处理                            │
│   ├─ ChatLogAdvisor: 记录对话日志                       │
│   ├─ RagCloudAdvisor: RAG检索增强                       │
│   └─ ChatTokenHandlerAdvisor: Token计数管理             │
└─────────────────┬───────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────────────┐
│            3. LLM 推理与规划                            │
│   - 分析用户意图                                         │
│   - 制定执行计划                                         │
│   - 选择合适工具                                         │
└─────────────────┬───────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────────────┐
│            4. 工具调用执行                               │
│   Step 1: WebSearchTools.search("红烧肉做法")           │
│   Step 2: 提取核心信息                                   │
│   Step 3: 生成结构化菜谱                                 │
└─────────────────┬───────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────────────┐
│            5. 最终总结输出                               │
│   - 应用 FinalSummaryPrompt                             │
│   - 格式化输出（菜名/食材/步骤/小贴士）                 │
│   - 保存到 ChatMemory                                   │
└─────────────────┬───────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────────────┐
│            6. SSE 流式返回                               │
│   🍳 【红烧肉】                                          │
│   简要介绍...                                            │
│   🥗 【食材清单】...                                     │
│   👩‍🍳 【制作步骤】...                                   │
│   💡 【烹饪小贴士】...                                   │
└─────────────────────────────────────────────────────────┘
```

---

## ⚡ 快速开始

### 前置要求

| 技术 | 版本要求 |
|:-----|:---------|
| ☕ **Java** | 21+ |
| 🐘 **PostgreSQL** | 12+ |
| 🗄️ **Redis** | 6+ |
| 🐰 **RabbitMQ** | 3.9+ |
| 📦 **Maven** | 3.8+ |

### 1. 克隆项目

```bash
git clone https://github.com/your-username/cookbook-app.git
cd cookbook-app/cookbook
```

### 2. 数据库初始化

**步骤 1：连接 PostgreSQL**

```bash
psql -U postgres
```

**步骤 2：创建数据库**

```sql
CREATE DATABASE ai_agent;
```

**步骤 3：执行初始化脚本**

```sql
\c ai_agent
\i database.sql
```

### 3. 配置文件

编辑 `src/main/resources/application-dev.yml`：

**核心配置项：**

| 配置项 | 说明 | 必填 |
|:------|:-----|:-----|
| `spring.datasource.*` | PostgreSQL 数据库连接 | ✅ |
| `spring.data.redis.*` | Redis 缓存配置 | ✅ |
| `spring.rabbitmq.*` | RabbitMQ 消息队列 | ✅ |
| `spring.ai.dashscope.api-key` | 阿里云通义千问 API Key | ✅ |
| `serpapi.key` | 网页搜索 API Key | ⚠️ 可选 |

**配置示例：**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ai_agent
    username: your_username
    password: your_password
  
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
  
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: your_rabbitmq_password
  
  ai:
    dashscope:
      api-key: your_dashscope_api_key  # 阿里云通义千问 API Key
      chat:
        options:
          model: qwen-max

# SerpAPI 配置（可选，用于网页搜索工具）
serpapi:
  key: your_serpapi_key
```

### 4. 启动应用

**方式 1：开发模式（推荐）**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**方式 2：打包运行**

```bash
# 打包应用
mvn clean package

# 运行 JAR 文件
java -jar target/cookbook-0.0.1-SNAPSHOT.jar
```

### 5. 访问应用

| 服务 | 地址 |
|:-----|:-----|
| 🌐 **应用地址** | http://localhost:8000/api/v1 |
| 📖 **API 文档** | http://localhost:8000/api/v1/doc.html |
| 📊 **健康检查** | http://localhost:8000/api/v1/actuator/health |
| 📈 **Prometheus** | http://localhost:8000/api/v1/actuator/prometheus |

---

## 🔧 配置说明

### 应用配置

| 配置项 | 说明 | 默认值 |
|:-------|:-----|:-------|
| `server.port` | 应用端口 | 8000 |
| `server.servlet.context-path` | 应用上下文路径 | /api/v1 |
| `spring.profiles.active` | 激活的配置文件 | local |

### AI 配置

| 配置项 | 说明 | 必填 |
|:-------|:-----|:-----|
| `spring.ai.dashscope.api-key` | 阿里云 DashScope API Key | ✅ |
| `spring.ai.dashscope.chat.options.model` | 使用的模型 | qwen-max |
| `serpapi.key` | SerpAPI Key（网页搜索） | ⚠️ 可选 |

### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:postgresql://host:port/database
    username: postgres
    password: ******
  
  ai:
    chat:
      memory:
        repository:
          jdbc:
            initialize-schema: always  # 自动初始化表结构
```

### Sa-Token 配置

```yaml
sa-token:
  token-name: Authorization
  timeout: 86400              # Token有效期（秒）
  is-concurrent: false        # 是否允许多端登录
  is-share: false            # 是否共用Token
  token-style: uuid
```

---

## 🐳 Docker 部署

### 1. 构建镜像

**步骤 1：打包应用**

```bash
mvn clean package -DskipTests
```

**步骤 2：复制 JAR 文件**

```bash
cp target/cookbook-0.0.1-SNAPSHOT.jar .
```

**步骤 3：构建 Docker 镜像**

```bash
docker build -t cookbook-app:latest .
```

---

## 📁 项目结构

```
cookbook/
├── src/
│   ├── main/
│   │   ├── java/com/ly/cookbook/
│   │   │   ├── advisor/              # Advisor 增强层
│   │   │   │   ├── ChatLogAdvisor.java
│   │   │   │   ├── RagCloudConfiguration.java
│   │   │   │   └── ChatTokenHandlerAdvisor.java
│   │   │   ├── agent/                # Agent 智能体
│   │   │   │   ├── BaseAgent.java
│   │   │   │   ├── ReActAgent.java
│   │   │   │   ├── ToolCallAgent.java
│   │   │   │   └── YiCookAgent.java
│   │   │   ├── aitools/              # AI 工具集
│   │   │   │   ├── FileTools.java
│   │   │   │   ├── ResourceDownloadTools.java
│   │   │   │   ├── TerminalOperationTools.java
│   │   │   │   ├── TerminateTools.java
│   │   │   │   ├── WebScriptTools.java
│   │   │   │   └── WebSearchTools.java
│   │   │   ├── auth/                 # 权限认证
│   │   │   │   └── StpInterfaceImpl.java
│   │   │   ├── cache/                # 缓存管理
│   │   │   │   └── RedisCache.java
│   │   │   ├── config/               # 配置类
│   │   │   │   ├── AiToolsConfiguration.java
│   │   │   │   ├── PgChatMemoryConfiguration.java
│   │   │   │   ├── RabbitMQConfiguration.java
│   │   │   │   ├── RedisConfig.java
│   │   │   │   └── SaTokenConfiguration.java
│   │   │   ├── controller/           # 控制器层
│   │   │   │   ├── AgentController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ChatController.java
│   │   │   │   ├── UserController.java
│   │   │   │   └── HealthController.java
│   │   │   ├── service/              # 业务服务层
│   │   │   ├── mapper/               # 数据访问层
│   │   │   ├── model/                # 数据模型
│   │   │   ├── exception/            # 异常处理
│   │   │   ├── handler/              # 全局处理器
│   │   │   └── CookbookApplication.java
│   │   └── resources/
│   │       ├── application.yml       # 主配置文件
│   │       ├── application-dev.yml   # 开发环境配置
│   │       ├── application-local.yml # 本地环境配置
│   │       ├── db/                   # 数据库脚本
│   │       │   ├── init-chat-memory.sql
│   │       │   ├── init-user.sql
│   │       │   └── README.md
│   │       ├── mapper/               # MyBatis XML
│   │       ├── prompt/               # AI 提示词
│   │       │   └── cookbook.txt
│   │       └── security/             # 安全配置
│   │           └── illegal-words.txt
│   └── test/                         # 测试代码
├── database.sql                      # 数据库初始化脚本
├── Dockerfile                        # Docker 镜像构建
├── pom.xml                          # Maven 依赖配置
└── README.md                        # 项目说明文档
```

---

## 💻 开发指南

### 添加新的 AI 工具

1. **创建工具类** `src/main/java/com/ly/cookbook/aitools/YourTool.java`

```java
@Component
@Description("工具描述")
public class YourTool {
    
    @Tool(name = "yourToolName", description = "工具功能描述")
    public String execute(
        @ToolParam(description = "参数描述") String param
    ) {
        // 实现工具逻辑
        return "执行结果";
    }
}
```

2. **注册到配置** `AiToolsConfiguration.java`

```java
@Bean
public ToolCallback yourToolCallback(YourTool yourTool) {
    return ToolCallback.builder()
        .function("yourToolName", yourTool::execute)
        .build();
}
```

### 自定义 Agent

继承 `BaseAgent` 或其子类：

```java
public class CustomAgent extends ToolCallAgent {
    
    public CustomAgent(ToolCallback[] tools, ChatModel chatModel, ChatMemory memory) {
        super(tools);
        
        // 设置系统提示
        setSystemPrompt("你是一个专业的...");
        
        // 配置 ChatClient
        ChatClient client = ChatClient.builder(chatModel)
            .defaultAdvisors(advisors)
            .build();
        setChatClient(client);
    }
    
    @Override
    public void clear() {
        // 清理逻辑
    }
}
```

### 数据库迁移

使用 `src/main/resources/db/` 目录管理 SQL 脚本：

```sql
-- CHANGELOG.md 记录变更历史
-- 新增脚本命名: V{version}_{description}.sql
-- 例如: V1.0.1_add_user_avatar.sql
```

### 本地开发

**常用命令：**

| 命令 | 说明 |
|:-----|:-----|
| `mvn spring-boot:run` | 启动应用（支持热重载） |
| `tail -f logs/cookbook.log` | 查看日志 |
| `mvn test` | 运行测试 |
| `mvn checkstyle:check` | 代码风格检查 |

**启动开发环境：**

```bash
# 启动热重载（需要配置 spring-boot-devtools）
mvn spring-boot:run

# 查看日志
tail -f logs/cookbook.log

# 运行测试
mvn test

# 代码风格检查
mvn checkstyle:check
```

---

## 📊 性能优化

### 1️⃣ Redis 缓存策略

| 缓存类型 | 过期时间 | 说明 |
|:---------|:---------|:-----|
| 用户会话缓存 | 30分钟 | 减少数据库查询 |
| API 响应缓存 | 根据业务设定 | 提高响应速度 |
| 热点数据预加载 | - | 预加载常用数据 |

### 2️⃣ 数据库优化

| 优化项 | 实现方式 |
|:------|:---------|
| 索引优化 | 已在 `database.sql` 中配置 |
| 连接池配置 | HikariCP（Spring Boot 默认） |
| 分页查询优化 | MyBatis Plus 分页插件 |

### 3️⃣ AI 调用优化

| 优化项 | 说明 |
|:------|:-----|
| Token 使用限制 | 控制单次请求 Token 数量 |
| 响应缓存 | 相同问题缓存结果 |
| 并发请求控制 | 限制并发调用 AI 接口 |

---

## 🔒 安全说明

### ⚠️ 敏感信息处理

| 要求 | 说明 |
|:-----|:-----|
| ⚠️ API Key 保护 | 不要将 API Key 提交到版本控制 |
| ⚠️ 环境变量 | 使用环境变量管理敏感配置 |
| ⚠️ 密码安全 | 生产环境务必修改默认密码 |

### 配置环境变量

**Linux / macOS：**

```bash
export DASHSCOPE_API_KEY=your_api_key
export SERPAPI_KEY=your_serpapi_key
export DB_PASSWORD=your_db_password
export REDIS_PASSWORD=your_redis_password
```

**Windows PowerShell：**

```powershell
$env:DASHSCOPE_API_KEY="your_api_key"
$env:SERPAPI_KEY="your_serpapi_key"
$env:DB_PASSWORD="your_db_password"
$env:REDIS_PASSWORD="your_redis_password"
```

### 安全防护措施

| 防护类型 | 实现方式 |
|:---------|:---------|
| **SQL 注入防护** | MyBatis 参数化查询 + 输入验证与过滤 |
| **XSS 防护** | 输出转义 + Content-Security-Policy 配置 |
| **CSRF 防护** | Sa-Token 集成 CSRF Token |
| **敏感词过滤** | `illegal-words.txt` 配置文件 |

---

## 🧪 测试

### 测试命令

| 命令 | 说明 |
|:-----|:-----|
| `mvn test` | 运行所有测试 |
| `mvn test -Dtest=AgentServiceTest` | 运行单个测试类 |
| `mvn clean package -DskipTests` | 跳过测试打包 |

**运行测试：**

```bash
# 运行所有测试
mvn test

# 运行单个测试类
mvn test -Dtest=AgentServiceTest

# 跳过测试打包
mvn clean package -DskipTests
```

---

## 📝 更新日志

### v0.0.1-SNAPSHOT (2025-10-27)

**🎉 初始版本发布**

| 功能 | 状态 |
|:-----|:----:|
| 集成 Spring AI 框架 | ✅ |
| 实现 YiCookAgent 智能体 | ✅ |
| 集成 6+ AI 工具 | ✅ |
| 用户认证与权限系统 | ✅ |
| 聊天记忆持久化 | ✅ |
| Docker 容器化支持 | ✅ |
| Prometheus 监控集成 | ✅ |

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

### 提交规范

| 类型 | 说明 |
|:-----|:-----|
| `feat` | 新功能 |
| `fix` | 修复问题 |
| `docs` | 文档更新 |
| `style` | 代码格式调整 |
| `refactor` | 重构 |
| `test` | 测试相关 |
| `chore` | 构建/工具链相关 |

**示例：**

```bash
git commit -m "feat: 添加新的AI工具"
git commit -m "fix: 修复登录认证问题"
git commit -m "docs: 更新README文档"
```

---

## 📄 许可证

本项目采用 [MIT License](LICENSE)

---

## 🙏 致谢

本项目基于以下优秀的开源项目构建：

| 项目 | 说明 |
|:-----|:-----|
| [Spring AI](https://docs.spring.io/spring-ai/reference/) | 强大的 AI 应用框架 |
| [Spring AI Alibaba](https://sca.aliyun.com/ai/) | 阿里云 AI 集成 |
| [通义千问](https://tongyi.aliyun.com/) | 阿里云大语言模型 |
| [Sa-Token](https://sa-token.cc/) | 轻量级权限认证框架 |
| [MyBatis Plus](https://baomidou.com/) | 持久层框架 |
| [Knife4j](https://doc.xiaominfo.com/) | API 文档工具 |

---

## 📮 联系方式

| 方式 | 链接 |
|:-----|:-----|
| 📧 **Email** | liuyia2022@163.com |
| 🐛 **Issue** | [GitHub Issues](https://github.com/your-username/cookbook-app/issues) |

---

<div align="center">

### Made with ❤️ by Liu Yi

⭐ **如果这个项目对你有帮助，请给一个 Star！** ⭐

</div>

