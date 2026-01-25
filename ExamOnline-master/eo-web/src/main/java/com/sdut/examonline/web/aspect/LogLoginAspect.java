package com.sdut.examonline.web.aspect;

import com.sdut.examonline.common.util.JsonUtils;
import com.sdut.examonline.common.util.SnowFlakeIdWorker;
import com.sdut.examonline.web.annotation.LogLogin;
import com.sdut.examonline.web.dto.LoginLogDto;
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
import java.util.Date;

@Slf4j
@Aspect
@Component
public class LogLoginAspect {

    public static final String KEY_MQ = CacheUtils.KEY_MESSAGAE_QUEUE_PREFIX + "loginlog";

    @Autowired
    CacheUtils cacheUtils;

    @Pointcut("@annotation(logLogin)")
    public void pointCutMethod(LogLogin logLogin) {

    }

    @Around("pointCutMethod(logLogin)")
    public Object doAround(ProceedingJoinPoint pjp, LogLogin logLogin) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        LoginLogDto logDto = new LoginLogDto();
        logDto.setId(SnowFlakeIdWorker.nextId());
        logDto.setType(logLogin.type().getValue());
        logDto.setIp(HttpUtils.getClientIp(request));
        logDto.setBrowser(HttpUtils.getBrowserName(request));
        logDto.setOs(HttpUtils.getOsName(request));

        try {
            Object result = pjp.proceed();
            if (result instanceof ResponseVo) {
                logDto.setMsg(((ResponseVo<?>) result).getMsg());
            }
            return result;
        } catch (Throwable e) {
            logDto.setMsg(e.getMessage());
            throw e;
        } finally {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                try {
                    UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
                    logDto.setUserId(Long.parseLong(String.valueOf(authentication.getCredentials())));
                    logDto.setUserName(String.valueOf(authentication.getPrincipal()));
                } catch (Exception ignored) {

                }
            }
            logDto.setCreateTime(new Date());
            cacheUtils.leftPush(KEY_MQ, JsonUtils.toJson(logDto));
        }
    }

}
