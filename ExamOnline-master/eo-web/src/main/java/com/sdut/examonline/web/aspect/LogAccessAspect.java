package com.sdut.examonline.web.aspect;

import com.google.common.collect.Sets;
import com.sdut.examonline.common.dto.BaseDto;
import com.sdut.examonline.common.exception.ServiceException;
import com.sdut.examonline.common.util.JsonUtils;
import com.sdut.examonline.common.util.SnowFlakeIdWorker;
import com.sdut.examonline.common.util.ValidationUtils;
import com.sdut.examonline.web.annotation.AccessLogIgnored;
import com.sdut.examonline.web.dto.AccessLogDto;
import com.sdut.examonline.web.util.CacheUtils;
import com.sdut.examonline.web.util.HttpUtils;
import com.sdut.examonline.web.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;

@Slf4j
@Aspect
@Component
public class LogAccessAspect {

    public static final String KEY_MQ = CacheUtils.KEY_MESSAGAE_QUEUE_PREFIX + "accesslog";

    private static final String IGNORED = "ignored";

    private static final Set<Class<?>> ignoredParamClasses = Sets.newHashSet(
            MultipartFile.class,
            HttpServletRequest.class,
            HttpServletResponse.class
    );

    @Autowired
    CacheUtils cacheUtils;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void pointCutMethod() {

    }

    @Before("pointCutMethod()")
    public void doBefore(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof BaseDto) {
                String result = ValidationUtils.validateEntity(arg);
                if (result != null) {
                    throw new ServiceException(400, result);
                }
            }
        }
    }

    @Around("pointCutMethod()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();

        AccessLogDto logDto = new AccessLogDto();
        logDto.setId(SnowFlakeIdWorker.nextId());
        logDto.setIp(HttpUtils.getClientIp(request));
        logDto.setUserAgent(request.getHeader("User-Agent"));
        logDto.setUri(new URI(request.getRequestURI()).getPath());
        logDto.setMethod(request.getMethod());
        logDto.setParams(getParams(pjp));

        long startTime = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            if (result instanceof ResponseVo) {
                ResponseVo<?> response = (ResponseVo<?>) result;
                logDto.setRespCode(response.getCode());
                logDto.setRespBody(JsonUtils.toJson(response.getData()));
            }
            return result;
        } catch (Throwable e) {
            logDto.setError(e.getMessage());
            String controller = pjp.getTarget().getClass().getSimpleName();
            if (!"ApiResourceController".equals(controller)) {
                log.info("<{}.{}>,args:{},error:{}", controller, pjp.getSignature().getName(), Arrays.toString(pjp.getArgs()), e.getMessage());
            }
            throw e;
        } finally {
            if (!method.isAnnotationPresent(AccessLogIgnored.class)) {
                long time = System.currentTimeMillis() - startTime;
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    try {
                        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
                        logDto.setUserId(Long.parseLong(String.valueOf(authentication.getCredentials())));
                        logDto.setUserName(String.valueOf(authentication.getPrincipal()));
                    } catch (Exception ignored) {

                    }
                }
                if (StringUtils.isNotEmpty(logDto.getError())) {
                    if (logDto.getError().length() > 128) {
                        logDto.setError(logDto.getError().substring(0, 128));
                    }
                }
                logDto.setTime(time);
                logDto.setCreateTime(new Date());
                cacheUtils.leftPush(KEY_MQ, JsonUtils.toJson(logDto));
            }
        }
    }

    public static String getParams(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        if (ArrayUtils.isEmpty(parameterNames)) {
            return null;
        }
        Map<String, Object> paramsMap = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                paramsMap.put(parameterNames[i], null);
                continue;
            }
            Class<?> clazz = arg.getClass();
            for (Class<?> ignoredParamClass : ignoredParamClasses) {
                if (ignoredParamClass.isAssignableFrom(clazz)) {
                    arg = IGNORED;
                    break;
                }
            }
            paramsMap.put(parameterNames[i], arg);
        }
        String params = "";
        try {
            params = JsonUtils.toJson(paramsMap);
        } catch (Exception e) {
            log.error("获取方法参数异常, class={},method={}", className, methodName, e);
        }
        return params;
    }

}
