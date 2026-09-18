package com.example.demo.common;

import com.example.demo.enums.ResultCodeEnum;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局返回对象包装
 * @Author: WangYuanrong
 * @Date: 2022/4/8 11:11
 */
@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        String path = serverHttpRequest.getURI().getPath();
        if (o instanceof Result || isFrameworkEndpoint(path)) {
            return o;
        }
        final Result<Object> result = Result.builder().success(Boolean.TRUE).code(ResultCodeEnum.SUCCESS.getCode()).msg(ResultCodeEnum.SUCCESS.getMsg()).data(o).build();
        // 解决包装String包装统一返回类型问题 方式二
//        if (o instanceof String) {
//            return JSON.toJSONString(result);
//        }
        return result;
    }

    private boolean isFrameworkEndpoint(String path) {
        return "/doc.html".equals(path) || "/swagger-ui.html".equals(path) || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs") || path.startsWith("/webjars/") || path.startsWith("/knife4j/")
                || path.startsWith("/actuator/");
    }
}
