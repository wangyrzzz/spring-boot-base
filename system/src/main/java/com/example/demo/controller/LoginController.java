package com.example.demo.controller;

import com.example.demo.common.JwtTokenService;
import com.example.demo.common.Result;
import com.example.demo.common.TokenPair;
import com.example.demo.dto.LoginDto;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.sesrvice.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {
    private final AuthService authService;
    private final JwtTokenService tokenService;

    @PostMapping("/login")
    public Result<TokenPair> login(@RequestParam Map<String, String> form,
                                   @RequestBody(required = false) LoginDto dto) {
        String username = value(form, dto == null ? null : dto.getUsername(), "username");
        String password = value(form, dto == null ? null : dto.getPassword(), "password");
        String clientId = value(form, dto == null ? null : dto.getClientId(), "client_id");
        return Result.ok(authService.login(username, password, clientId));
    }

    @PostMapping("/refresh")
    public Result<TokenPair> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return Result.ok(tokenService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization,
                               @RequestBody(required = false) RefreshTokenRequest request) {
        tokenService.revoke(tokenService.bearerToken(authorization), request == null ? null : request.getRefreshToken());
        return Result.ok();
    }

    private String value(Map<String, String> form, String jsonValue, String name) {
        if ("client_id".equals(name)) {
            String camel = form.get("clientId");
            if (org.springframework.util.StringUtils.hasText(camel)) return camel;
        }
        String formValue = form.get(name);
        return org.springframework.util.StringUtils.hasText(formValue) ? formValue : jsonValue;
    }
}
