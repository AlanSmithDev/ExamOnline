package com.sdut.examonline.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Chen
 * @since 2022/4/8 8:37
 */
@Getter
@RequiredArgsConstructor
public enum LoginLogType {

    LOG_IN(0),
    LOG_OUT(1);

    private final Integer value;

}
