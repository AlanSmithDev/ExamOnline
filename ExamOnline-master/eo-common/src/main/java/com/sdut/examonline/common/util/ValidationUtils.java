package com.sdut.examonline.common.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.Set;

/**
 * @author Chen
 * @since 2022/3/13 18:29
 */
public class ValidationUtils {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> String validateEntity(T object) {
        Set<ConstraintViolation<T>> set = VALIDATOR.validate(object, Default.class);
        if (set != null && !set.isEmpty()) {
            for (ConstraintViolation<T> constraintViolation : set) {
                return constraintViolation.getMessage();
            }
        }
        return null;
    }

    public static <T> String validateProperty(T object, String propertyName) {
        Set<ConstraintViolation<T>> set = VALIDATOR.validateProperty(object, propertyName, Default.class);
        if (set != null && !set.isEmpty()) {
            for (ConstraintViolation<T> constraintViolation : set) {
                return constraintViolation.getMessage();
            }
        }
        return null;
    }

}
