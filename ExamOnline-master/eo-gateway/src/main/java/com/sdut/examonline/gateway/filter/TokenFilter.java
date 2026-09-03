package com.sdut.examonline.gateway.filter;

import com.sdut.examonline.common.constant.JwtConstants;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.util.JwtTokenUtils;
import com.sdut.examonline.dubbo.client.UserDubboService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author Chen
 * @since 2022/3/16 15:43
 */
@Component
public class TokenFilter implements GlobalFilter, Ordered {

    private static final String ERROR_RESPONSE = "{\"code\":%d,\"msg\":\"%s\"}";

    @Reference(check = false)
    UserDubboService userDubboService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String accessToken = exchange.getRequest().getHeaders().getFirst(JwtConstants.HEADER_ACCESS_TOKEN);
        if (StringUtils.isEmpty(accessToken)) {
            return chain.filter(exchange);
        }
        String internalToken = userDubboService.getInternalToken(accessToken);
        if (StringUtils.isEmpty(internalToken) || JwtTokenUtils.isExpiration(internalToken)) {
            return sendError(exchange, ErrorCode.TOKEN_EXPIRATION);
        }

        Long userId = JwtTokenUtils.getUserId(internalToken);
        String userName = JwtTokenUtils.getUserName(internalToken);
        String userRole = JwtTokenUtils.getUserRole(internalToken);

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(userRole)) {
            return sendError(exchange, ErrorCode.FORBIDDEN);
        }

        ServerWebExchange.Builder builder = exchange.mutate();
        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate();
        requestBuilder.header(JwtConstants.HEADER_INTERNAL_TOKEN, internalToken);
        requestBuilder.header(JwtConstants.HEADER_USER_ID, String.valueOf(userId));
        requestBuilder.header(JwtConstants.HEADER_USER_NAME, userName);
        requestBuilder.header(JwtConstants.HEADER_USER_ROLE, userRole);
        builder.request(requestBuilder.build());

        return chain.filter(builder.build());
    }

    private Mono<Void> sendError(ServerWebExchange webExchange, ErrorCode errorCode) {
        ServerHttpResponse response = webExchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        byte[] bytes = String.format(ERROR_RESPONSE, errorCode.getCode(), errorCode.getMsg()).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }

}
