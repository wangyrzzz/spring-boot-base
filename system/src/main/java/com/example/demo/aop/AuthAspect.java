package com.example.demo.aop;

import com.example.demo.annotation.PreAuth;
import com.example.demo.common.ApiException;
import com.example.demo.common.AuthExpressionRoot;
import com.example.demo.common.RbacPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/** Evaluates the common {@link PreAuth} authorization expression. */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@org.springframework.core.annotation.Order(Ordered.HIGHEST_PRECEDENCE + 100)
@ConditionalOnProperty(prefix = "sys.rbac", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AuthAspect {
    private final RbacPermissionService permissionService;
    private final ApplicationContext applicationContext;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer parameterNames = new DefaultParameterNameDiscoverer();

    @Around("@annotation(com.example.demo.annotation.PreAuth) || @within(com.example.demo.annotation.PreAuth)")
    public Object preAuth(ProceedingJoinPoint point) throws Throwable {
        PreAuth annotation = findAnnotation(point);
        if (annotation == null || evaluate(annotation.value(), point)) {
            return point.proceed();
        }
        throw new ApiException(403, "无权访问");
    }

    private boolean evaluate(String expression, ProceedingJoinPoint point) {
        if (!StringUtils.hasText(expression)) {
            return false;
        }
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        AuthExpressionRoot root = new AuthExpressionRoot(permissionService);
        StandardEvaluationContext context = new MethodBasedEvaluationContext(
                root, method, point.getArgs(), parameterNames);
        context.setBeanResolver(new BeanFactoryResolver(applicationContext));
        try {
            Boolean allowed = expressionParser.parseExpression(expression).getValue(context, Boolean.class);
            return Boolean.TRUE.equals(allowed);
        } catch (RuntimeException ex) {
            log.warn("RBAC鉴权表达式执行失败, method={}, expression={}", method, expression, ex);
            return false;
        }
    }

    private PreAuth findAnnotation(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        PreAuth annotation = AnnotatedElementUtils.findMergedAnnotation(method, PreAuth.class);
        if (annotation != null) {
            return annotation;
        }
        Class<?> targetClass = AopUtils.getTargetClass(point.getTarget());
        annotation = AnnotatedElementUtils.findMergedAnnotation(targetClass, PreAuth.class);
        if (annotation != null) {
            return annotation;
        }
        return AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), PreAuth.class);
    }
}
