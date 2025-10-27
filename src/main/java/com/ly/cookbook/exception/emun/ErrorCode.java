package com.ly.cookbook.exception.emun;


import com.ly.cookbook.exception.error.ErrorType;
import lombok.Getter;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/27 23:02
 * @email liuyia2022@163.com
 */
@Getter
public enum ErrorCode implements ErrorType {
    SUCCESS(200, "成功","sys.global.success"),

    SYSTEM_BUSY(100003, "系统繁忙,请稍候再试", "sys.global.busy"),

    GATEWAY_ERROR(200001, "网关异常", "sys.gateway.error"),
    GATEWAY_TIME_OUT(200002, "网关超时", "sys.gateway.time_out"),

    PARAMS_ERROR(40000, "参数错误","sys.param.exception"),
    NOT_FOUND_ERROR(40400, "请求数据不存在","sys.mongo.empty"),
    FORBIDDEN_ERROR(40300, "禁止访问","sys.sec.forbind"),


    SYSTEM_ERROR(50000, "系统内部异常","sys.unknown.exception"),
    OPERATION_ERROR(50001, "操作失败","sys.global.fail"),
    PARAM_FILE_SIZE(500005, "上传文件大小超过限制", "sys.param.file_size_limit"),
    PARAM_TOKEN(500004, "无效Token", "sys.param.token_invalid"),

    SERVICE(600000, "业务异常", "sys.service.exception"),
    SERVICE_NOT_SUPPORT(600001, "业务不支持", "sys.service.not_support"),
    SERVICE_INFO_LOSE(600002, "业务信息缺失", "sys.service.info_lose"),
    SERVICE_POWER_LIMIT(600003, "业务权限限制", "sys.service.power_limit"),

    MONGO(710000, "数据异常", "sys.mongo.exception"),
    MONGO_EMPTY(710001, "数据为空", "sys.mongo.empty"),
    MONGO_REPEAT(710002, "数据重复", "sys.mongo.repeat"),
    MONGO_EXCEPTION(710003, "数据操作异常", "sys.mongo.operate_exception"),

    SQL(700000, "数据库数据异常", "sys.sql.exception"),
    SQL_EMPTY(700001, "数据库数据为空", "sys.sql.empty"),
    SQL_REPEAT(700002, "数据库数据重复", "sys.sql.repeat"),
    SQL_EXCEPTION(700003, "数据库数据操作异常", "sys.sql.operate_exception"),
    SQL_PRIMARY_KEY(700003, "数据库唯一键冲突", "sys.sql.primary_key"),

    API(800000, "第三方接口异常", "sys.api.exception"),
    API_FAIL(800001, "第三方接口请求失败", "sys.api.fail"),
    API_EXCEPTION(800002, "第三方接口请求异常", "sys.api.other_exception"),

    LOGIN_FAIL(900000, "用户登录失败", "sys.login.login.fail"),
    NOT_LOGIN_ERROR(900001, "用户未登录", "sys.login.no_login"),
    NO_AUTH_ERROR(900002, "无权限", "sys.login.no_power"),
    NO_ACCOUNT(900003, "账号不存在", "sys.login.no_account"),
    ACCOUNT_EXCEPTION(900004, "用户信息异常", "sys.login.account_exception"),
    EXCEPTION(900005, "未知异常", "sys.unknown.exception");




    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 信息
     */
    private final String usMessage;
    private final String cnMessage;

    ErrorCode(int code, String cnMessage, String usMessage) {
        this.code = code;
        this.usMessage  = usMessage;
        this.cnMessage = cnMessage;
    }

    @Override
    public String getCnMessage() {
        return this.cnMessage;
    }

    @Override
    public String getUsMessage() {
        return this.usMessage;
    }
}
