package com.example.demo.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService tokenService;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher matcher = new AntPathMatcher();
    private final List<String> publicPaths = List.of(
            "/", "/auth/login", "/auth/refresh", "/error", "/favicon.ico",
            "/doc.html", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**", "/knife4j/**",
            "/actuator/**");

    public AuthenticationFilter(JwtTokenService tokenService, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || publicPaths.stream().anyMatch(path -> matcher.match(path, request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = tokenService.bearerToken(request.getHeader("Authorization"));
        if (!StringUtils.hasText(token)) {
            unauthorized(response);
            return;
        }
        try {
            AuthUserContext.set(tokenService.validateAccess(token));
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException ex) {
            unauthorized(response);
        } finally {
            AuthUserContext.clear();
        }
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Result.exception(401, "未登录或令牌无效")));
    }
}
