package com.example.demo.controller;

import com.example.demo.common.JwtTokenService;
import com.example.demo.common.Result;
import com.example.demo.common.TokenPair;
import com.example.demo.sesrvice.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/retail-auth/oauth")
@RequiredArgsConstructor
public class OAuthTokenController {
    private final AuthService authService;
    private final JwtTokenService tokenService;

    @PostMapping("/token")
    public Result<Map<String, Object>> token(@RequestParam Map<String, String> form,
                                             @RequestBody(required = false) Map<String, Object> json) {
        Map<String, Object> body = json == null ? Map.of() : json;
        String grantType = value(form, body, "grant_type", "grantType");
        String clientId = value(form, body, "client_id", "clientId");
        String clientSecret = value(form, body, "client_secret", "clientSecret");
        TokenPair pair = "client_credentials".equalsIgnoreCase(grantType)
                ? authService.clientCredentials(clientId, clientSecret)
                : authService.login(value(form, body, "username"), value(form, body, "password"), clientId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("access_token", pair.getAccessToken());
        result.put("token_type", pair.getTokenType());
        result.put("expires_in", pair.getAccessTokenExpiresIn());
        if (pair.getRefreshToken() != null) {
            result.put("refresh_token", pair.getRefreshToken());
            result.put("refresh_expires_in", pair.getRefreshTokenExpiresIn());
        }
        return Result.ok(result);
    }

    @GetMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization,
                               @RequestParam(value = "refresh_token", required = false) String refreshToken) {
        tokenService.revoke(tokenService.bearerToken(authorization), refreshToken);
        return Result.ok();
    }

    private String value(Map<String, String> form, Map<String, Object> json, String formName, String jsonName) {
        String value = form.get(formName);
        if (StringUtils.hasText(value)) {
            return value;
        }
        Object jsonValue = json.get(jsonName);
        return jsonValue == null ? null : String.valueOf(jsonValue);
    }

    private String value(Map<String, String> form, Map<String, Object> json, String name) {
        return value(form, json, name, name);
    }
}
