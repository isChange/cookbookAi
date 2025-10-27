package com.ly.cookbook.exception.emun;


import com.ly.cookbook.exception.error.ErrorType;
import lombok.Getter;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/29 10:16
 * @email liuyia2022@163.com
 */
@Getter
public enum AssertEnum implements ErrorType {
    //*************************** 用户信息 ***************************
    USER_STATUS_DISABLE("用户已禁用", "user.status.disable"),
    USER_PERMISSION_LOSE("用户系统权限缺失", "user.permission.lose"),
    USER_INFO_LOSE("用户没有找到", "user.info.lose"),
    USER_GROUP_LOSE("用户组没有找到", "user.info.lose"),
    USER_INFO_NO_EXIST("用户不存在", "user.info.no_exist"),
    PERMISSION_DENIED("权限不足", "user.permission.denied"),
    //*************************** LocalDate信息 ***************************
    LOCAL_DATE_CONVERT_ERROR("LocalDateTime转换失败", "date.local.convert_error"),
    //*************************** LocalDateTime信息 ***************************
    LOCAL_DATETIME_CONVERT_ERROR("LocalDateTime转换失败", "local.datetime.convert_error"),
    //*************************** 文件信息 ***************************
    FILE_TYPE_ERROR("文件上传类型不合法", "file.type.error"),
    FILE_SIZE_LIMIT("文件大小超过限制", "file.size.limit"),
    FILE_DATA_EMPTY("文件数据为空", "file.data.empty"),
    FILE_GET_BY_URL_ERROR("根据URL获取文件信息失败", "file.get.by.url.fail"),
    FILE_UNKNOWN_ERROR("文件上传失败","file.unknown.error"),
    //**************************** 其他信息 ***************************
    OTHER_NUMBER_ERROR("数字格式错误","The number format is invalid."),
    OTHER_NUMBER_IS_NULL("数字不能为空","The number must not be null."),
    PARAMS_EMPTY ("参数不能为空", "params.empty");
    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 信息
     */
    private final String usMessage;
    private final String cnMessage;

    AssertEnum(String usMessage, String cnMessage) {
        this.code = ErrorCode.PARAMS_ERROR.getCode();
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
