package com.sdut.examonline.web.vo;

import com.sdut.examonline.common.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ApiModel(value = "返回结果", description = "返回结果")
public class ResponseVo<T> extends BaseVo {

    private static final Integer CODE_SUCCESS = 200;

    private static final String MSG_SUCCESS = "操作成功";

    private static final Integer CODE_FAILURE = 400;

    private static final String MSG_FAILURE = "操作失败";

    private static final String MSG_DISALLOW = "权限不足";

    @ApiModelProperty(value = "响应代码", required = true)
    private final Integer code;

    @ApiModelProperty(value = "响应消息")
    private final String msg;

    @ApiModelProperty(value = "响应内容")
    private final T data;

    public static ResponseVo<Boolean> judge(boolean success) {
        return success ? success() : failure();
    }

    public static <T> ResponseVo<T> success() {
        return newResponse(CODE_SUCCESS, MSG_SUCCESS, null);
    }

    public static <T> ResponseVo<T> success(T data) {
        return newResponse(CODE_SUCCESS, MSG_SUCCESS, data);
    }

    public static <T> ResponseVo<T> success(String msg, T data) {
        return newResponse(CODE_SUCCESS, msg, data);
    }

    public static <T> ResponseVo<T> failure() {
        return newResponse(CODE_FAILURE, MSG_FAILURE, null);
    }

    public static <T> ResponseVo<T> failure(T data) {
        return newResponse(CODE_FAILURE, MSG_FAILURE, data);
    }

    public static <T> ResponseVo<T> failure(String msg, T data) {
        return newResponse(CODE_FAILURE, msg, data);
    }

    public static <T> ResponseVo<T> disallow() {
        return newResponse(CODE_FAILURE, MSG_DISALLOW, null);
    }

    public static <T> ResponseVo<T> disallow(T data) {
        return newResponse(CODE_FAILURE, MSG_DISALLOW, data);
    }

    public static <T> ResponseVo<T> newResponse(Integer code, String msg, T data) {
        return new ResponseVo<>(code, msg, data);
    }
}
