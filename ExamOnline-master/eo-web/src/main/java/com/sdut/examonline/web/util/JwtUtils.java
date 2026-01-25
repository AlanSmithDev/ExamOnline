package com.sdut.examonline.web.util;

import com.sdut.examonline.common.constant.JwtConstants;
import com.sdut.examonline.common.util.JwtTokenUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Chen
 * @since 2022/3/15 19:02
 */
public class JwtUtils {

    public static String getAccessToken() {
        return getAccessToken(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    public static String getAccessToken(HttpServletRequest request) {
        return request.getHeader(JwtConstants.HEADER_ACCESS_TOKEN);
    }

    public static String getInternalToken() {
        return getUserRole(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    public static String getInternalToken(HttpServletRequest request) {
        return request.getHeader(JwtConstants.HEADER_INTERNAL_TOKEN);
    }

    public static Long getUserId() {
        return getUserId(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    public static Long getUserId(HttpServletRequest request) {
        return Long.parseLong(request.getHeader(JwtConstants.HEADER_USER_ID));
    }

    public static String getUserName() {
        return getUserName(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    public static String getUserName(HttpServletRequest request) {
        return request.getHeader(JwtConstants.HEADER_USER_NAME);
    }

    public static String getUserRole() {
        return getUserRole(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    public static String getUserRole(HttpServletRequest request) {
        return request.getHeader(JwtConstants.HEADER_USER_ROLE);
    }

    public static Integer getPlatform() {
        return getPlatform(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    public static Integer getPlatform(HttpServletRequest request) {
        return JwtTokenUtils.getPlatform(getInternalToken(request));
    }

}
