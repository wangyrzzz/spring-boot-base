package com.example.demo.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DataAuth {
    String code() default "";
    String column() default "create_dept";
    DataScopeEnum type() default DataScopeEnum.ALL;
    String field() default "*";
    String value() default "";
}
