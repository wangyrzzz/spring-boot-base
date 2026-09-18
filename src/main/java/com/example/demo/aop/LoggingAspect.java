package com.example.demo.aop;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 使用 aop 切面记录请求日志信息
 * </p>
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Autowired
    private Environment environment;

    private static final String START_TIME = "request-start";

    /**
     * 切入点
     */
    @Pointcut("execution(* com.example.demo.controller.*.*(..))")
    public void executeResource() {

    }

    /**
     * 前置操作
     *
     * @param point 切入点
     */
    @Before("executeResource()")
    public void beforeLog(JoinPoint point) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        boolean sensitive = isSensitiveEndpoint(request);
        log.info("【URL】：{}", request.getRequestURL());
        log.info("【IP】：{}", request.getRemoteAddr());
        log.info("【Class】：{}，【Method】：{}", point.getSignature().getDeclaringTypeName(), point.getSignature().getName());
        if (sensitive) {
            log.info("【Payload】：[REDACTED]");
        } else if (point.getArgs() != null) {
            final List<Object> args = Arrays.stream(point.getArgs())
                    .filter(s -> !(s instanceof HttpServletRequest))
                    .filter(s -> !(s instanceof HttpServletResponse))
                    .collect(Collectors.toList());
            log.info("【Payload】：{}，", JSON.toJSONString(args));
        }
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("【Parameters】：{}，", sensitive ? "[REDACTED]" : JSON.toJSONString(parameterMap));
        Long start = System.currentTimeMillis();
        request.setAttribute(START_TIME, start);
    }

    /**
     * 环绕操作
     *
     * @param point 切入点
     * @return 原方法返回值
     * @throws Throwable 异常信息
     */
    @Around("executeResource()")
    public Object aroundLog(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes == null ? null : attributes.getRequest();
        log.info("【Response】：{}", request != null && isSensitiveEndpoint(request) ? "[REDACTED]" : JSON.toJSONString(result));
        return result;
    }

    private boolean isSensitiveEndpoint(HttpServletRequest request) {
        String path = request.getServletPath();
        return "/auth/login".equals(path) || "/auth/refresh".equals(path) || "/auth/logout".equals(path);
    }

    /**
     * 后置操作
     */
    @AfterReturning("executeResource()")
    public void afterReturning() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        String userAgent = request.getHeader("User-Agent");
        Long start = (Long) request.getAttribute(START_TIME);
        Long end = System.currentTimeMillis();
        log.info("【Time】：{}ms", end - start);
        log.info("【Environment】：{}", environment.getActiveProfiles()[0]);
        log.info("【User-Agent】：{}", userAgent);
    }
}

