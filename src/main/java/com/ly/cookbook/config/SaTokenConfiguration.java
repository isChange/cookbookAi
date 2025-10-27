package com.ly.cookbook.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 配置类 (适配 Reactor 版本)
 * 使用 sa-token-reactor-spring-boot3-starter
 * 支持从 URL 参数中读取 Token（用于 EventSource SSE）
 *
 * @author admin
 * @date 2025-10-11
 */
@Slf4j
@Configuration
public class SaTokenConfiguration implements WebMvcConfigurer {
    /**
     * 注册 Sa-Token 拦截器（用于普通同步请求）
     * 注意：SSE 接口完全排除，在 Controller 方法中手动验证
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，直接执行登录校验
        // 不使用 SaRouter，避免在异步上下文中访问 SaHolder 导致上下文未初始化异常
        registry.addInterceptor(new SaInterceptor(handle -> {
                    if ("OPTIONS".equals(SaHolder.getRequest().getMethod())) {
                        return;
                    }
                    StpUtil.checkLogin();
                }))
                .addPathPatterns("/**")
                // 排除不需要登录的路由
                .excludePathPatterns(
                        "/auth/login",                          // 登录接口
                        "/auth/logout",                         // 登出接口
                        "/user/register",                       // 用户注册接口
                        "/user/check-username",                 // 检查用户名接口
                        "/register-code/validate",              // 验证注册码（注册时用）
                        "/health",                              // 健康检查
                        "/doc.html",                            // Knife4j 文档
                        "/swagger-ui.html",                     // Swagger 文档
                        "/swagger-ui/**",                       // Swagger UI 资源
                        "/swagger-resources/**",                // Swagger 资源
                        "/v3/api-docs/**",                      // OpenAPI 3.0 文档
                        "/webjars/**",                          // Webjars 资源
                        "/favicon.ico",                         // 网站图标
                        "/error",                               // 错误页面
                        "/chat/simple/stream/sse",              // 普通聊天 SSE 接口
                        "/chat/memory/stream/sse",              // 记忆对话 SSE 接口
                        "/agent/yicook/stream/sse",             // Agent 对话 SSE 接口
                        "/actuator/**"                          // Actuator 监控端点（包括 Prometheus）
                );
    }

}

