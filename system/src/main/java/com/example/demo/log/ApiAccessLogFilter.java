package com.example.demo.log;

import com.example.demo.common.AuthenticatedUser;
import com.example.demo.entity.SysLogApi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ApiAccessLogFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;
    private final Environment environment;
    private final ApiLogPublisher publisher;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!shouldLog(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper requestWrapper = request instanceof ContentCachingRequestWrapper cachingRequest
                ? cachingRequest : new ContentCachingRequestWrapper(request, -1);
        ContentCachingResponseWrapper responseWrapper = response instanceof ContentCachingResponseWrapper cachingResponse
                ? cachingResponse : new ContentCachingResponseWrapper(response);
        long started = System.nanoTime();
        Throwable failure = null;
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } catch (IOException | ServletException | RuntimeException ex) {
            failure = ex;
            throw ex;
        } finally {
            try {
                publisher.publish(buildLog(requestWrapper, responseWrapper, started, failure));
            } finally {
                responseWrapper.copyBodyToResponse();
            }
        }
    }

    private SysLogApi buildLog(ContentCachingRequestWrapper request,
                               ContentCachingResponseWrapper response,
                               long started,
                               Throwable failure) {
        SysLogApi log = new SysLogApi();
        AuthenticatedUser user = (AuthenticatedUser) request.getAttribute(ApiLogCaptureInterceptor.USER_ATTRIBUTE);
        if (user != null) {
            log.setCreateBy(user.getUserId());
            log.setCreateDept(user.getDeptId());
        }
        Date now = new Date();
        log.setCreateTime(now);
        log.setUpdateTime(now);
        log.setUpdateBy(log.getCreateBy());
        log.setServiceName(environment.getProperty("spring.application.name", "spring-boot-base"));
        log.setEnv(String.join(",", environment.getActiveProfiles()));
        log.setServerHost(hostName());
        log.setServerIp(request.getLocalAddr());
        log.setType("HTTP");
        log.setTitle("接口访问");
        log.setMethod(request.getMethod());
        log.setRequestUri(request.getRequestURI());
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setRequestIp(requestIp(request));
        log.setMethodClass((String) request.getAttribute(ApiLogCaptureInterceptor.HANDLER_CLASS_ATTRIBUTE));
        log.setMethodName((String) request.getAttribute(ApiLogCaptureInterceptor.HANDLER_METHOD_ATTRIBUTE));
        log.setRequestParams(requestParams(request));
        log.setResponseParams(responseParams(response));
        log.setDurationMs((System.nanoTime() - started) / 1_000_000L);
        log.setHttpStatus(response.getStatus());
        log.setSuccess(failure == null && response.getStatus() < 400 ? 1 : 0);
        log.setErrorMessage(failure == null ? null : failure.getMessage());
        log.setDeleted(0);
        return log;
    }

    private String requestParams(ContentCachingRequestWrapper request) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("query", request.getParameterMap());
        byte[] body = request.getContentAsByteArray();
        if (body.length > 0) {
            params.put("body", readableBody(body, request.getContentType()));
        }
        return json(params);
    }

    private String responseParams(ContentCachingResponseWrapper response) {
        return readableBody(response.getContentAsByteArray(), response.getContentType());
    }

    private String readableBody(byte[] body, String contentType) {
        if (body.length == 0) {
            return null;
        }
        if (isText(contentType)) {
            return new String(body, StandardCharsets.UTF_8);
        }
        return json(Map.of("contentType", contentType == null ? "unknown" : contentType,
                "size", body.length, "binary", true));
    }

    private boolean isText(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return true;
        }
        String lower = contentType.toLowerCase();
        return lower.startsWith("text/")
                || lower.contains("json")
                || lower.contains("xml")
                || lower.contains("form-urlencoded");
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            return String.valueOf(value);
        }
    }

    private boolean shouldLog(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String path = request.getRequestURI();
        return !path.startsWith("/actuator")
                && !path.equals("/favicon.ico")
                && !path.equals("/doc.html")
                && !path.startsWith("/swagger-ui/")
                && !path.startsWith("/v3/api-docs")
                && !path.startsWith("/webjars/")
                && !path.startsWith("/knife4j/");
    }

    private String requestIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String real = request.getHeader("X-Real-IP");
        return real == null || real.isBlank() ? request.getRemoteAddr() : real;
    }

    private String hostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception ignored) {
            return null;
        }
    }
}
