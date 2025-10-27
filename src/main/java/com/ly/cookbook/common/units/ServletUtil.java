package com.ly.cookbook.common.units;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Description: Servlet工具类
 *
 * @author: liuyi
 * @date: 2024-08-01 09:10
 */
public class ServletUtil {

    /**
     * 获取attributes
     */
    private static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }


    /**
     * 获取Head参数
     */
    public static String getHeader(String header) {
        return getRequest().getHeader(header);
    }

    /**
     * 获取String参数, 并设置默认值
     */
    public static String getParameter(String name, String defaultValue) {
        return StringUtils.defaultIfBlank(getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name) {
        return NumberUtils.toInt(getParameter(name));
    }

    /**
     * 获取Integer参数, 并设置默认值
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return NumberUtils.toInt(getParameter(name), defaultValue);
    }

    /**
     * 获取Boolean参数
     */
    public static Boolean getParameterToBool(String name) {
        return Boolean.getBoolean(getParameter(name));
    }

    /**
     * 将字符串渲染到客户端
     */
    @SneakyThrows(Exception.class)
    public static void renderString(HttpServletResponse response, String str) {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().print(str);
    }

    /**
     * 获取客户端IP地址
     */
    public static String getClientIP() {
        return getClientIP(getRequest());
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HttpServletRequest
     * @return IP地址
     */
    public static String getClientIP(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多级代理的情况，取第一个IP
        if (StringUtils.isNotBlank(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        // 本地IPv6地址转换
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    /**
     * 获取浏览器类型
     */
    public static String getBrowser() {
        return getBrowser(getRequest());
    }

    /**
     * 获取浏览器类型
     *
     * @param request HttpServletRequest
     * @return 浏览器类型
     */
    public static String getBrowser(HttpServletRequest request) {
        if (request == null) {
            return "Unknown";
        }
        String userAgent = request.getHeader("User-Agent");
        if (StringUtils.isBlank(userAgent)) {
            return "Unknown";
        }

        userAgent = userAgent.toLowerCase();
        
        if (userAgent.contains("edg")) {
            return "Microsoft Edge";
        } else if (userAgent.contains("chrome") && !userAgent.contains("edg")) {
            return "Google Chrome";
        } else if (userAgent.contains("firefox")) {
            return "Mozilla Firefox";
        } else if (userAgent.contains("safari") && !userAgent.contains("chrome")) {
            return "Apple Safari";
        } else if (userAgent.contains("opera") || userAgent.contains("opr")) {
            return "Opera";
        } else if (userAgent.contains("msie") || userAgent.contains("trident")) {
            return "Internet Explorer";
        } else {
            return "Unknown";
        }
    }

    /**
     * 获取操作系统
     */
    public static String getOS() {
        return getOS(getRequest());
    }

    /**
     * 获取操作系统
     *
     * @param request HttpServletRequest
     * @return 操作系统
     */
    public static String getOS(HttpServletRequest request) {
        if (request == null) {
            return "Unknown";
        }
        String userAgent = request.getHeader("User-Agent");
        if (StringUtils.isBlank(userAgent)) {
            return "Unknown";
        }

        userAgent = userAgent.toLowerCase();
        
        if (userAgent.contains("windows nt 10.0")) {
            return "Windows 10/11";
        } else if (userAgent.contains("windows nt 6.3")) {
            return "Windows 8.1";
        } else if (userAgent.contains("windows nt 6.2")) {
            return "Windows 8";
        } else if (userAgent.contains("windows nt 6.1")) {
            return "Windows 7";
        } else if (userAgent.contains("windows")) {
            return "Windows";
        } else if (userAgent.contains("mac")) {
            return "MacOS";
        } else if (userAgent.contains("linux")) {
            return "Linux";
        } else if (userAgent.contains("android")) {
            return "Android";
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
            return "iOS";
        } else {
            return "Unknown";
        }
    }

    /**
     * 获取User-Agent
     */
    public static String getUserAgent() {
        return getUserAgent(getRequest());
    }

    /**
     * 获取User-Agent
     *
     * @param request HttpServletRequest
     * @return User-Agent
     */
    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        return StringUtils.defaultString(request.getHeader("User-Agent"), "");
    }
}
