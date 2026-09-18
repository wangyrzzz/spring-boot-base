package com.example.demo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义获取当前用户参数注解
 */
//Annotation所修饰的对象范围:方法参数
@Target({ElementType.PARAMETER,ElementType.METHOD})
//Annotation被保留时间:运行时保留(有效)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginUser {
    String value() default "user" ;
}
