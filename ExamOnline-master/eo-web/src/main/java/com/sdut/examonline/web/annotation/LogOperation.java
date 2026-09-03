package com.sdut.examonline.web.annotation;

import com.sdut.examonline.common.constant.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Chen
 * @since 2022/4/8 8:34
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogOperation {

    String msg();

    OperationType type();

    boolean ignoreResult() default false;

}
