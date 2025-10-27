# 使用 OpenJDK 21 的精简版作为基础镜像
FROM openjdk:21-jdk-slim

# 设置维护者信息
LABEL maintainer="cookbook-app"
LABEL description="Cookbook Application - AI Recipe Agent"

# 设置工作目录
WORKDIR /app

# 创建非 root 用户运行应用（安全最佳实践）
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 复制打包好的 JAR 文件到容器中
# 假设你在本地执行 mvn clean package 后生成的 JAR 文件
COPY cookbook-0.0.1-SNAPSHOT.jar /app/app.jar

# 创建日志目录
RUN mkdir -p /app/logs && \
    mkdir -p /app/temp/download && \
    chown -R appuser:appuser /app

# 切换到非 root 用户
USER appuser

# 暴露应用端口
EXPOSE 8000


# 启动应用
# 使用 exec 形式确保 Java 进程是容器的主进程（PID 1）
# 如需自定义 JVM 参数，可通过环境变量 JAVA_OPTS 传入
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]

