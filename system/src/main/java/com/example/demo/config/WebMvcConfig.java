package com.example.demo.config;

import com.example.demo.aop.LoginUserMethodArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: WangYuanrong
 * @Date: 2021/4/8 11:37
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 解决包装String包装统一返回类型问题方式一
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        List<MappingJackson2HttpMessageConverter> list = new ArrayList<>();
        final Iterator<HttpMessageConverter<?>> iterator = converters.iterator();
        while (iterator.hasNext()) {
            final HttpMessageConverter<?> converter = iterator.next();
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                list.add((MappingJackson2HttpMessageConverter) converter);
                iterator.remove();
            }
        }
        converters.addAll(0, list);
    }

    /**
     * 开启跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路由
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedOriginPatterns("*")
                .allowedMethods("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(authInterceptor).addPathPatterns("/**")
//                .excludePathPatterns("/v1/user/login", "/v1/swagger-resources/**", "/v1/v2/**", "/v1/webjars/**", "/v1/doc.html");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        // 注册loginUserMethodArgumentResolver的参数分解器
        argumentResolvers.add(loginUserMethodArgumentResolver());
    }

    @Bean
    public HandlerMethodArgumentResolver loginUserMethodArgumentResolver(){
        return new LoginUserMethodArgumentResolver();
    }


    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }
}
