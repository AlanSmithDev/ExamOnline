package com.sdut.examonline.web.security;

import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.util.JsonUtils;
import com.sdut.examonline.web.vo.ResponseVo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationHandler implements AccessDeniedHandler, AuthenticationEntryPoint {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException {
        sendError(response);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException {
        sendError(response);
    }

    private void sendError(HttpServletResponse response) throws IOException {
        String json = JsonUtils.toJson(ResponseVo.newResponse(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMsg(), null));
        if (json != null) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(json);
        }
    }

}

