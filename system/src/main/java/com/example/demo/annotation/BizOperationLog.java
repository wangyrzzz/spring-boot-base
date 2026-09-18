package com.example.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BizOperationLog {
    String bizType() default "";
    String bizName() default "";
    String bizId() default "";
    String operationType() default "";
    String relatedBillId() default "";
    String relatedBillNo() default "";
    boolean recordParams() default true;
    boolean recordResult() default true;
    boolean recordDiff() default true;
}
