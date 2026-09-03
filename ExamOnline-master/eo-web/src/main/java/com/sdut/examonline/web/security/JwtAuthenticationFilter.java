package com.sdut.examonline.web.security;

import com.google.common.collect.Lists;
import com.sdut.examonline.common.constant.JwtConstants;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    /**
     * 上游网关层已对token鉴权
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String accessToken = request.getHeader(JwtConstants.HEADER_ACCESS_TOKEN);
        String internalToken = request.getHeader(JwtConstants.HEADER_INTERNAL_TOKEN);

        if (StringUtils.isEmpty(accessToken) || StringUtils.isEmpty(internalToken)) {
            chain.doFilter(request, response);
            return;
        }
        String userId = request.getHeader(JwtConstants.HEADER_USER_ID);
        String userName = request.getHeader(JwtConstants.HEADER_USER_NAME);
        String userRole = request.getHeader(JwtConstants.HEADER_USER_ROLE);
        if (StringUtils.isEmpty(userId)
                || StringUtils.isEmpty(userName)
                || StringUtils.isEmpty(userRole)) {
            throw new ServiceException(ErrorCode.BAD_REQUEST);
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName, userId, Lists.newArrayList(new SimpleGrantedAuthority(userRole)));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        super.doFilterInternal(request, response, chain);
    }

}

