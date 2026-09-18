package com.example.demo.aop;

import com.example.demo.annotation.LoginUser;
import com.example.demo.common.AuthUserContext;
import com.example.demo.common.AuthenticatedUser;
import com.example.demo.dto.LoginUserDTO;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginUserMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class)
                && LoginUserDTO.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
                                  NativeWebRequest request, WebDataBinderFactory factory) {
        AuthenticatedUser user = AuthUserContext.required();
        LoginUserDTO dto = new LoginUserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getAccount());
        dto.setRealName(user.getNickName());
        return dto;
    }
}
