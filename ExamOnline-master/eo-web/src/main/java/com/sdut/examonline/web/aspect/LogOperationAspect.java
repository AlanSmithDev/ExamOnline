package com.sdut.examonline.web.aspect;

import com.sdut.examonline.web.constant.OperationState;
import com.sdut.examonline.common.util.JsonUtils;
import com.sdut.examonline.common.util.SnowFlakeIdWorker;
import com.sdut.examonline.web.annotation.LogOperation;
import com.sdut.examonline.web.dto.OperationLogDto;
import com.sdut.examonline.web.util.CacheUtils;
import com.sdut.examonline.web.util.HttpUtils;
import com.sdut.examonline.web.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Date;

@Slf4j
@Aspect
@Component
public class LogOperationAspect {

    public static final String KEY_MQ = CacheUtils.KEY_MESSAGAE_QUEUE_PREFIX + "operlog";

    @Autowired
    CacheUtils cacheUtils;

    @Pointcut("@annotation(logOperation)")
    public void pointCutMethod(LogOperation logOperation) {

    }

    @Around("pointCutMethod(logOperation)")
    public Object doAround(ProceedingJoinPoint pjp, LogOperation logOperation) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        OperationLogDto logDto = new OperationLogDto();
        logDto.setId(SnowFlakeIdWorker.nextId());
        logDto.setIp(HttpUtils.getClientIp(request));
        logDto.setType(logOperation.type().getValue());
        logDto.setMsg(logOperation.msg());
        logDto.setUserAgent(request.getHeader("User-Agent"));
        logDto.setUri(new URI(request.getRequestURI()).getPath());
        logDto.setMethod(request.getMethod());
        logDto.setParams(LogAccessAspect.getParams(pjp));

        long startTime = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            if (result instanceof ResponseVo) {
                if (((ResponseVo<?>) result).getCode() == 200) {
                    logDto.setState(OperationState.SUCCESS);
                    return result;
                }
            }
            logDto.setState(OperationState.FAILURE);
            return result;
        } catch (Throwable e) {
            logDto.setState(OperationState.FAILURE);
            throw e;
        } finally {
            long time = System.currentTimeMillis() - startTime;
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                try {
                    UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
                    logDto.setUserId(Long.parseLong(String.valueOf(authentication.getCredentials())));
                    logDto.setUserName(String.valueOf(authentication.getPrincipal()));
                } catch (Exception ignored) {

                }
            }
            logDto.setTime(time);
            logDto.setCreateTime(new Date());
            cacheUtils.leftPush(KEY_MQ, JsonUtils.toJson(logDto));
        }
    }

}
