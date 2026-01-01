package com.sdut.examonline.common.constant;

/**
 * @author Chen
 * @since 2022/3/5 16:49
 */
public class RoleConstants {

    public static final String ADMIN = "admin";

    public static final String TEACHER = "teacher";

    public static final String STUDENT = "student";

    public static final String ANNOTATION_ADMIN_ONLY = "hasAuthority('" + ADMIN + "')";

    public static final String ANNOTATION_ADMIN_OR_TEACHER = "hasAnyAuthority('" + ADMIN + "','" + TEACHER + "')";

}
