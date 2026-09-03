package com.sdut.examonline.common.util;

import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.exception.ServiceException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;

public class Assert {

    public static void isTrue(boolean expression, int code, String msg, Object... params) {
        if (!expression) {
            throw newException(code, String.format(msg, params));
        }
    }

    public static void isTrue(boolean expression, ErrorCode errorCode) {
        isTrue(expression, errorCode.getCode(), errorCode.getMsg());
    }

    public static void isFalse(boolean expression, ErrorCode errorCode) {
        isTrue(!expression, errorCode.getCode(), errorCode.getMsg());
    }

    public static void isFalse(boolean expression, int code, String msg, Object... params) {
        isTrue(!expression, code, msg, params);
    }

    public static void isNull(Object object, ErrorCode errorCode) {
        isTrue(object == null, errorCode.getCode(), errorCode.getMsg());
    }

    public static void isNull(Object object, int code, String msg, Object... params) {
        isTrue(object == null, code, msg, params);
    }

    public static void notNull(Object object, ErrorCode errorCode) {
        isTrue(object != null, errorCode.getCode(), errorCode.getMsg());
    }

    public static void notNull(Object object, int code, String msg, Object... params) {
        isTrue(object != null, code, msg, params);
    }

    public static void isEmpty(String value, ErrorCode errorCode) {
        isTrue(StringUtils.isEmpty(value), errorCode.getCode(), errorCode.getMsg());
    }

    public static void isEmpty(String value, int code, String msg, Object... params) {
        isTrue(StringUtils.isEmpty(value), code, msg, params);
    }

    public static void notEmpty(String value, ErrorCode errorCode) {
        isFalse(StringUtils.isEmpty(value), errorCode.getCode(), errorCode.getMsg());
    }

    public static void notEmpty(String value, int code, String msg, Object... params) {
        isFalse(StringUtils.isEmpty(value), code, msg, params);
    }

    public static void isEmpty(Map<?, ?> map, ErrorCode errorCode) {
        isTrue(map == null || map.isEmpty(), errorCode.getCode(), errorCode.getMsg());
    }

    public static void isEmpty(Map<?, ?> map, int code, String msg, Object... params) {
        isTrue(map == null || map.isEmpty(), code, msg, params);
    }

    public static void notEmpty(Map<?, ?> map, ErrorCode errorCode) {
        isFalse(map == null || map.isEmpty(), errorCode.getCode(), errorCode.getMsg());
    }

    public static void notEmpty(Map<?, ?> map, int code, String msg, Object... params) {
        isFalse(map == null || map.isEmpty(), code, msg, params);
    }

    public static void isEmpty(Collection<?> collection, ErrorCode errorCode) {
        isTrue(collection == null || collection.isEmpty(), errorCode.getCode(), errorCode.getMsg());
    }

    public static void isEmpty(Collection<?> collection, int code, String msg, Object... params) {
        isTrue(collection == null || collection.isEmpty(), code, msg, params);
    }

    public static void notEmpty(Collection<?> collection, ErrorCode errorCode) {
        isFalse(collection == null || collection.isEmpty(), errorCode.getCode(), errorCode.getMsg());
    }

    public static void notEmpty(Collection<?> collection, int code, String msg, Object... params) {
        isFalse(collection == null || collection.isEmpty(), code, msg, params);
    }

    public static void isEmpty(Object[] array, ErrorCode errorCode) {
        isTrue(ArrayUtils.isEmpty(array), errorCode.getCode(), errorCode.getMsg());
    }

    public static void isEmpty(Object[] array, int code, String msg, Object... params) {
        isTrue(ArrayUtils.isEmpty(array), code, msg, params);
    }

    public static void notEmpty(Object[] array, ErrorCode errorCode) {
        isTrue(ArrayUtils.isNotEmpty(array), errorCode.getCode(), errorCode.getMsg());
    }

    public static void notEmpty(Object[] array, int code, String msg, Object... params) {
        isTrue(ArrayUtils.isNotEmpty(array), code, msg, params);
    }

    private static ServiceException newException(int code, String msg) {
        return new ServiceException(code, msg);
    }

}
