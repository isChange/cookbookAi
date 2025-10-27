package com.ly.cookbook.handler;




import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.ly.cookbook.common.model.Result;
import com.ly.cookbook.exception.AssertException;
import com.ly.cookbook.exception.BaseException;
import com.ly.cookbook.exception.emun.ErrorCode;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description 全局异常处理
 * @createDate：2025/5/29 10:23
 * @email liuyia2022@163.com
 */
@RestControllerAdvice
@Slf4j
@Hidden
public class GlobalExceptionHandler {
    @Resource
    private HttpServletRequest request;

    @ExceptionHandler(value = {AssertException.class})
    public Result alertException(AssertException ex) {
        log.error("请求地址: {}; Assert参数拦截异常为: {}", request.getRequestURI(), ex.getMessage());
        return Result.fail(ex.getErrorType());
    }

    @ExceptionHandler(value = Exception.class)
    public Result handle(Exception e) {
        log.error("请求地址: {}; 全局异常信息ex={}", e.getMessage(), e);
        if (e instanceof BaseException) {
            BaseException baseException = (BaseException) e;
            return Result.fail(baseException.getErrorType(), baseException.getMessage());
        }
        return Result.fail(ErrorCode.EXCEPTION);
    }

    @ExceptionHandler(value = NotLoginException.class)
    public Result handle(NotLoginException e) {
        log.error("全局异常信息ex={}", e.getMessage(), e);
        return Result.fail(ErrorCode.NOT_LOGIN_ERROR);
    }

    @ExceptionHandler(value = NotPermissionException.class)
    public Result handle(NotPermissionException e) {
        log.error("全局异常信息ex={}", e.getMessage(), e);
        return Result.fail(ErrorCode.NO_AUTH_ERROR);
    }

    @ExceptionHandler(value = NotRoleException.class)
    public Result handle(NotRoleException e) {
        log.error("全局异常信息ex={}", e.getMessage(), e);
        return Result.fail(ErrorCode.NO_AUTH_ERROR);
    }

}
