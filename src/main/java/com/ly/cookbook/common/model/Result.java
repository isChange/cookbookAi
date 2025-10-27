package com.ly.cookbook.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.ly.cookbook.common.constant.SysConstant;
import com.ly.cookbook.exception.emun.ErrorCode;
import com.ly.cookbook.exception.error.ErrorType;
import com.ly.cookbook.common.units.ServletUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/27 23:01
 * @email liuyia2022@163.com
 */
@Getter
@ToString
@Schema(description = "通用返回结果")
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 7505175748532929242L;
    @Schema(description = "状态码", example = "0")
    private Integer code;
    
    @Schema(description = "返回消息", example = "成功")
    private String message;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "时间戳", example = "2025-09-17 12:00:00")
    private Instant time;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "返回数据")
    private T data;
    public Result() {
        this.time = ZonedDateTime.now().toInstant();
    }

    /**
     * @param type
     * @param data
     */
    public Result(ErrorType type, T data) {
        this(type);
        this.data = data;
    }

    /**
     * @param errorType
     */
    public Result(ErrorType errorType) {
        String language = ServletUtil.getHeader(SysConstant.LANGUAGE);
        if (Objects.equals(language, SysConstant.LANGUAGE_EN_US)) {
            this.message = errorType.getUsMessage();
        } else {
            this.message = errorType.getCnMessage();
        }
        this.code = errorType.getCode();
        this.time = ZonedDateTime.now().toInstant();
    }

    /**
     * 快速创建成功结果
     */
    public static Result success() {
        return success(null);
    }

    /**
     * 快速创建成功结果并返回结果数据
     */
    public static Result success(Object data) {
        return new Result<>(ErrorCode.SUCCESS, data);
    }

    /**
     * 系统异常类没有返回数据
     */
    public static Result fail() {
        return new Result(ErrorCode.OPERATION_ERROR);
    }

    /**
     * 系统异常类并返回结果数据
     */
    public static Result fail(ErrorType errorType) {
        return Result.fail(errorType, null);
    }

    /**
     * 系统异常类并返回结果数据
     */
    public static Result fail(Object data) {
        return new Result<>(ErrorCode.OPERATION_ERROR, data);
    }

    /**
     * 系统异常类并返回结果数据
     */
    public static Result fail(ErrorType errorType, Object data) {
        return new Result<>(errorType, data);
    }

    /**
     * 成功
     *
     * @return true/false
     */
    @JsonIgnore
    public boolean isSuccess() {
        return Objects.equals(ErrorCode.SUCCESS.getCode(), this.code);
    }

    /**
     * 失败
     *
     * @return true/false
     */
    @JsonIgnore
    public boolean isFail() {
        return !isSuccess();
    }
}
