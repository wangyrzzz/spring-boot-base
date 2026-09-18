package com.example.demo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防重复提交注解
 * @Author: WangYuanrong
 * @Date: 2021/6/24 9:36
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Resubmit {

    /**
     * 锁时间，毫秒
     * @return
     */
    int lockTime() default 2000;

}
