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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {
    private final AuthService authService;
    private final JwtTokenService tokenService;

    @PostMapping("/login")
    public Result<TokenPair> login(@RequestBody @Valid LoginDto dto) {
        return Result.ok(authService.login(dto.getUsername(), dto.getPassword()));
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
}
