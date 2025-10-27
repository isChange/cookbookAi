package com.ly.cookbook.exception;



import com.ly.cookbook.exception.emun.ErrorCode;
import com.ly.cookbook.exception.error.ErrorType;
import lombok.Getter;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/29 10:08
 * @email liuyia2022@163.com
 */
@Getter
public class BaseException extends RuntimeException{
    private final ErrorType errorType;

    /**
     * 默认是系统异常
     */
    public BaseException() {
        this.errorType = ErrorCode.SYSTEM_ERROR;
    }
    public BaseException(ErrorType errorType) {
        this.errorType = errorType;
    }

    public BaseException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public BaseException(ErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }
}
