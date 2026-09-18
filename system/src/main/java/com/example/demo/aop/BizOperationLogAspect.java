package com.example.demo.aop;

import com.example.demo.annotation.BizOperationLog;
import com.example.demo.common.AuthUserContext;
import com.example.demo.common.AuthenticatedUser;
import com.example.demo.system.OperationLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class BizOperationLogAspect {
    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer parameterNames = new DefaultParameterNameDiscoverer();

    @Around("@annotation(annotation)")
    public Object around(ProceedingJoinPoint point, BizOperationLog annotation) throws Throwable {
        long started = System.currentTimeMillis();
        Map<String, Object> record = base(point, annotation);
        Object result;
        try {
            result = point.proceed();
        } catch (Throwable ex) {
            record.put("success", 0);
            record.put("errorMessage", ex.getMessage());
            record.put("durationMs", System.currentTimeMillis() - started);
            safeSave(record);
            throw ex;
        }
        record.put("durationMs", System.currentTimeMillis() - started);
        if (annotation.recordResult()) record.put("resultData", json(result));
        record.put("afterSnapshot", annotation.recordDiff() ? json(result) : null);
        record.put("changeSummary", annotation.recordDiff() ? "业务操作完成" : null);
        Runnable save = () -> safeSave(record);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { save.run(); }
            });
        } else {
            save.run();
        }
        return result;
    }

    private Map<String, Object> base(ProceedingJoinPoint point, BizOperationLog annotation) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Map<String, Object> record = new LinkedHashMap<>();
        record.put("bizType", annotation.bizType());
        record.put("bizName", annotation.bizName());
        record.put("operationType", annotation.operationType());
        record.put("bizId", spel(annotation.bizId(), signature, point.getArgs()));
        record.put("relatedBillId", spel(annotation.relatedBillId(), signature, point.getArgs()));
        record.put("relatedBillNo", spel(annotation.relatedBillNo(), signature, point.getArgs()));
        AuthenticatedUser user = AuthUserContext.get();
        if (user != null) {
            record.put("operatorId", user.getUserId());
            record.put("operatorName", user.getNickName() == null ? user.getAccount() : user.getNickName());
            record.put("roleName", user.getRoleName());
        }
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            record.put("requestPath", request.getRequestURI());
            record.put("httpMethod", request.getMethod());
            record.put("ip", request.getRemoteAddr());
            record.put("deviceType", request.getHeader("User-Agent"));
        }
        record.put("methodClass", signature.getDeclaringTypeName());
        record.put("methodName", signature.getName());
        if (annotation.recordParams()) record.put("requestParams", json(Arrays.asList(point.getArgs())));
        if (annotation.recordDiff()) record.put("beforeSnapshot", json(Arrays.asList(point.getArgs())));
        return record;
    }

    private Object spel(String expression, MethodSignature signature, Object[] args) {
        if (expression == null || expression.isBlank()) return null;
        try {
            StandardEvaluationContext context = new StandardEvaluationContext();
            String[] names = parameterNames.getParameterNames(signature.getMethod());
            if (names != null) for (int i = 0; i < names.length && i < args.length; i++) context.setVariable(names[i], args[i]);
            return expressionParser.parseExpression(expression).getValue(context);
        } catch (RuntimeException ex) {
            log.debug("Unable to resolve operation log SpEL {}", expression, ex);
            return null;
        }
    }

    private String json(Object value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            return json.replaceAll("(?i)(\\\"(?:password|secret|token|authorization|clientSecret)\\\"\\s*:\\s*)\\\"[^\\\"]*\\\"", "$1\\\"***\\\"");
        } catch (Exception ex) { return String.valueOf(value); }
    }

    private void safeSave(Map<String, Object> record) {
        try { operationLogService.save(record); } catch (RuntimeException ex) { log.warn("业务日志写入失败，不影响原业务", ex); }
    }
}
