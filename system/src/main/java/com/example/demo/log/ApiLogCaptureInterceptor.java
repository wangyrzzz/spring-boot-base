package com.example.demo.log;

import com.example.demo.common.AuthUserContext;
import com.example.demo.common.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class ApiLogCaptureInterceptor implements HandlerInterceptor {
    public static final String USER_ATTRIBUTE = ApiLogCaptureInterceptor.class.getName() + ".user";
    public static final String HANDLER_CLASS_ATTRIBUTE = ApiLogCaptureInterceptor.class.getName() + ".handlerClass";
    public static final String HANDLER_METHOD_ATTRIBUTE = ApiLogCaptureInterceptor.class.getName() + ".handlerMethod";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        AuthenticatedUser user = AuthUserContext.get();
        if (user != null) {
            request.setAttribute(USER_ATTRIBUTE, user);
        }
        if (handler instanceof HandlerMethod handlerMethod) {
            request.setAttribute(HANDLER_CLASS_ATTRIBUTE, handlerMethod.getBeanType().getName());
            request.setAttribute(HANDLER_METHOD_ATTRIBUTE, handlerMethod.getMethod().getName());
        }
        return true;
    }
}
