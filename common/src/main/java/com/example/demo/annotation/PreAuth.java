package com.example.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declarative authorization expression for a controller or service method.
 *
 * <p>The expression is evaluated against the common authorization functions
 * exposed by {@code AuthExpressionRoot}, for example
 * {@code hasRole('admin')} or {@code hasPermission('system:user:write')}.</p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PreAuth {
    String value();
}
