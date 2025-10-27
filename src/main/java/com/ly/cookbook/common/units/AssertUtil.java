package com.ly.cookbook.common.units;



import com.ly.cookbook.common.constant.SysConstant;
import com.ly.cookbook.exception.AssertException;
import com.ly.cookbook.exception.emun.ErrorCode;
import com.ly.cookbook.exception.error.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/29 10:10
 * @email liuyia2022@163.com
 */
@Slf4j
public class AssertUtil {
    /**
     * 直接添加异常信息
     */
    public static void putMeg(ErrorType type) {
        log.error(type.getCnMessage());
        throw new AssertException(type);
    }

    /**
     * 为真值
     */
    public static void isTrue(Boolean value, ErrorType type) {
        if (Boolean.FALSE.equals(value)) {
            throw new AssertException(type);
        }
    }

    /**
     * 为假值
     */
    public static void isFalse(Boolean value, ErrorType type) {
        if (Boolean.TRUE.equals(value)) {
            throw new AssertException(type);
        }
    }

    /**
     * 相等
     *
     * @param expected 期望值
     * @param actual   实际值
     */
    public static void isEquals(Object expected, Object actual, ErrorType type) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertException(type);
        }
    }

    /**
     * 不相等
     *
     * @param expected 期望值
     * @param actual   实际值
     */
    public static void isNotEquals(Object expected, Object actual, ErrorType type) {
        if (Objects.equals(expected, actual)) {
            throw new AssertException(type);
        }
    }

    /**
     * 普通对象为空
     */
    public static void isNull(Object value, ErrorType type) {
        if (Objects.nonNull(value)) {
            throw new AssertException(type);
        }
    }

    /**
     * 批量判断对象是否为空
     */
    public static void isNotNull(ErrorType type, Object... values){
        for (Object value : values) {
            if(value instanceof String){
                isNotBlank(value.toString(), type);
            }else if(value instanceof Boolean){
                isTrue((Boolean) value, type);
            }else if(value instanceof Number){
                notMinus((Number) value,true, type);
            }else if(value instanceof Collection){
                Collection collection = (Collection) value;
                if(CollectionUtils.isEmpty(collection)){
                    throw new AssertException(type);
                }
            } else if(Objects.isNull(value)){
                throw new AssertException(type);
            }
        }
    }

    /**
     * 普通对象不为空
     */
    public static void isNotNull(Object value, ErrorType type) {
        if (Objects.isNull(value)) {
            throw new AssertException(type);
        }
    }

    /**
     * 字符串为空
     */
    public static void isBlank(String value, ErrorType type) {
        if (StringUtils.isNotBlank(value)) {
            throw new AssertException(type);
        }
    }

    /**
     * 字符串不为空
     */
    public static void isNotBlank(String value, ErrorType type) {
        if (StringUtils.isBlank(value)) {
            throw new AssertException(type);
        }
    }

    /**
     * 集合为空
     */
    public static void isEmpty(Collection value, ErrorType type) {
        if (!CollectionUtils.isEmpty(value)) {
            throw new AssertException(type);
        }
    }

    /**
     * 集合不为空
     */
    public static void isNotEmpty(Collection value, ErrorType type) {
        if (CollectionUtils.isEmpty(value)) {
            throw new AssertException(type);
        }
    }

    /**
     * 判断数字是否为有效数字
     */
    public static void notMinus(Number number,boolean notMinus, ErrorType type) {
        if (number == null) {
            throw new AssertException(ErrorCode.PARAMS_ERROR);
        }
        boolean result;
        // 使用instanceof检查和类型转换来优化性能
        if (number instanceof Byte) {
            result = notMinus ? number.byteValue() < SysConstant.INT_ZERO : number.byteValue() <= SysConstant.INT_ZERO;
        } else if (number instanceof Short) {
            result = notMinus ? number.shortValue() < SysConstant.INT_ZERO :  number.shortValue() <= SysConstant.INT_ZERO;
        } else if (number instanceof Integer) {
            result = notMinus ? number.intValue() < SysConstant.INT_ZERO : number.intValue() <= SysConstant.INT_ZERO;
        } else if (number instanceof Long) {
            result = notMinus ? number.longValue() < SysConstant.LONG_ZERO : number.longValue() <= SysConstant.LONG_ZERO;
        } else if (number instanceof Float) {
            result = notMinus ? number.floatValue() < SysConstant.FLOAT_ZERO : number.floatValue() <= SysConstant.FLOAT_ZERO;
        } else if (number instanceof Double) {
            result = notMinus ? number.doubleValue() < SysConstant.DOUBLE_ZERO : number.doubleValue() <= SysConstant.DOUBLE_ZERO;
        } else if (number instanceof BigDecimal) {
            result = notMinus ? ((BigDecimal) number).compareTo(BigDecimal.ZERO) < SysConstant.INT_ZERO : ((BigDecimal) number).compareTo(BigDecimal.ZERO) <= SysConstant.INT_ZERO;
        } else {
            result = notMinus ? number.doubleValue() < SysConstant.DOUBLE_ZERO : number.doubleValue() <= SysConstant.DOUBLE_ZERO;
        }
        if (result) {
            throw new AssertException(type);
        }
    }

    public static void notMinus(Number number, ErrorType type) {
        notMinus(number,true, type);
    }
}
