package com.example.demo.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtTokenService {
    private static final String REFRESH_PREFIX = "auth:refresh:";
    private static final String SESSION_REFRESH_PREFIX = "auth:session:refresh:";
    private static final DefaultRedisScript<Long> CONSUME_REFRESH = new DefaultRedisScript<>(
            "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end", Long.class);

    private final AuthProperties properties;
    private final StringRedisTemplate redis;
    private final SecretKey signingKey;
    private final ClientCredentialService clientCredentialService;

    @Autowired
    public JwtTokenService(AuthProperties properties, ObjectProvider<StringRedisTemplate> redisProvider,
                           ClientCredentialService clientCredentialService) {
        this(properties, redisProvider.getIfAvailable(), clientCredentialService);
    }

    public JwtTokenService(AuthProperties properties, StringRedisTemplate redis,
                           ClientCredentialService clientCredentialService) {
        this.properties = properties;
        this.redis = redis;
        this.clientCredentialService = clientCredentialService;
        if (!StringUtils.hasText(properties.getJwtKey())
                || properties.getJwtKey().getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("sys.auth.jwt-key must contain at least 32 bytes");
        }
        this.signingKey = Keys.hmacShaKeyFor(properties.getJwtKey().getBytes(StandardCharsets.UTF_8));
    }

    public TokenPair issue(AuthenticatedUser user) {
        return issue(user, user == null ? null : user.getClientCode(),
                properties.getAccessTokenTtlSeconds(), properties.getRefreshTokenTtlSeconds());
    }

    public TokenPair issue(AuthenticatedUser user, String clientCode, long accessTtlSeconds, long refreshTtlSeconds) {
        return issue(user, clientCode, accessTtlSeconds, refreshTtlSeconds, null);
    }

    private TokenPair issue(AuthenticatedUser user, String clientCode, long accessTtlSeconds, long refreshTtlSeconds,
                            String existingSessionId) {
        if (user == null || !StringUtils.hasText(clientCode)) {
            throw new IllegalArgumentException("客户端编码不能为空");
        }
        String accessJti = UUID.randomUUID().toString();
        String sessionId = StringUtils.hasText(existingSessionId) ? existingSessionId : UUID.randomUUID().toString();
        Date now = new Date();
        Date accessExpiry = new Date(now.getTime() + accessTtlSeconds * 1000);
        String access = buildToken(user, clientCode, accessJti, sessionId, "access", now, accessExpiry);
        String refresh = null;
        Long refreshExpiresIn = null;
        if (properties.isRefreshTokenEnabled()) {
            String refreshJti = UUID.randomUUID().toString();
            Date refreshExpiry = new Date(now.getTime() + refreshTtlSeconds * 1000);
            refresh = buildToken(user, clientCode, refreshJti, sessionId, "refresh", now, refreshExpiry);
            refreshExpiresIn = refreshTtlSeconds;
            if (redis != null) {
                redis.opsForValue().set(REFRESH_PREFIX + refreshJti, refresh, Duration.ofSeconds(refreshTtlSeconds));
                redis.opsForValue().set(SESSION_REFRESH_PREFIX + sessionId, refreshJti,
                        Duration.ofSeconds(refreshTtlSeconds));
            }
        }
        return new TokenPair(access, refresh, "Bearer", accessTtlSeconds, refreshExpiresIn);
    }

    public AuthenticatedUser validateAccess(String token) {
        Claims claims = parse(token);
        if (!"access".equals(claims.get("token_type", String.class))) {
            throw new JwtException("not an access token");
        }
        requireClientCode(claims);
        return toUser(claims);
    }

    public TokenPair refresh(String token) {
        if (!properties.isRefreshTokenEnabled()) {
            throw new ApiException(400, "Refresh Token 未启用");
        }
        Claims claims = parse(token);
        if (!"refresh".equals(claims.get("token_type", String.class))) {
            throw new JwtException("not a refresh token");
        }
        if (redis != null) {
            Long consumed = redis.execute(CONSUME_REFRESH,
                    Collections.singletonList(REFRESH_PREFIX + claims.getId()), token);
            if (!Long.valueOf(1L).equals(consumed)) {
                throw new JwtException("refresh token already used or revoked");
            }
        }
        String clientCode = requireClientCode(claims);
        ClientPolicy policy = clientCredentialService.requireActive(clientCode);
        return issue(toUser(claims), clientCode, policy.accessTokenValidity(), policy.refreshTokenValidity(),
                claims.get("session_id", String.class));
    }

    public void revoke(String accessToken, String refreshToken) {
        if (!properties.isRefreshTokenEnabled() || redis == null) {
            return;
        }
        String sessionId = sessionId(accessToken);
        if (!StringUtils.hasText(sessionId)) {
            sessionId = sessionId(refreshToken);
        }
        if (StringUtils.hasText(sessionId)) {
            String refreshJti = redis.opsForValue().get(SESSION_REFRESH_PREFIX + sessionId);
            if (StringUtils.hasText(refreshJti)) {
                redis.delete(REFRESH_PREFIX + refreshJti);
            }
            redis.delete(SESSION_REFRESH_PREFIX + sessionId);
        }
        deleteTokenKey(refreshToken, REFRESH_PREFIX);
    }

    public String bearerToken(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        if (!authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return null;
        }
        String token = authorization.substring(7).trim();
        return StringUtils.hasText(token) ? token : null;
    }

    private void deleteTokenKey(String token, String prefix) {
        try {
            Claims claims = parse(token);
            redis.delete(prefix + claims.getId());
        } catch (RuntimeException ignored) {
            // Logout remains idempotent for expired or malformed tokens.
        }
    }

    private String buildToken(AuthenticatedUser user, String clientCode, String jti, String sessionId, String type,
                              Date issuedAt, Date expiry) {
        var builder = Jwts.builder().id(jti).subject(String.valueOf(user.getUserId()))
                .claim("session_id", sessionId).claim("token_type", type).claim("client_code", clientCode)
                .claim("user_id", user.getUserId()).claim("account", user.getAccount())
                .claim("user_name", user.getUserName()).claim("nick_name", user.getNickName())
                .claim("dept_id", user.getDeptId())
                .claim("full_dept_id", user.getFullDeptId()).claim("post_id", user.getPostId())
                .claim("role_name", user.getRoleName()).claim("role_ids", user.getRoleIds())
                .claim("role_codes", user.getRoleCodes())
                .claim("detail", user.getDetail()).issuedAt(issuedAt).expiration(expiry);
        return builder.signWith(signingKey).compact();
    }

    private String sessionId(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            return parse(token).get("session_id", String.class);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private Claims parse(String token) {
        if (!StringUtils.hasText(token)) {
            throw new JwtException("empty token");
        }
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }

    private AuthenticatedUser toUser(Claims claims) {
        List<Long> roleIds = new ArrayList<>();
        Object rawRoleIds = claims.get("role_ids");
        if (rawRoleIds instanceof Iterable<?> iterable) {
            for (Object item : iterable) {
                if (item != null) {
                    roleIds.add(Long.valueOf(String.valueOf(item)));
                }
            }
        }
        List<String> roleCodes = new ArrayList<>();
        Object rawRoleCodes = claims.get("role_codes");
        if (rawRoleCodes instanceof Iterable<?> iterable) {
            for (Object item : iterable) {
                if (item != null && StringUtils.hasText(String.valueOf(item))) {
                    roleCodes.add(String.valueOf(item));
                }
            }
        }
        Map<String, Object> detail = new HashMap<>();
        Object rawDetail = claims.get("detail");
        if (rawDetail instanceof Map<?, ?> map) {
            map.forEach((key, value) -> detail.put(String.valueOf(key), value));
        }
        return AuthenticatedUser.builder().userId(asLong(claims.get("user_id")))
                .clientCode(claims.get("client_code", String.class)).account(claims.get("account", String.class))
                .userName(claims.get("user_name", String.class)).nickName(claims.get("nick_name", String.class))
                .deptId(asLong(claims.get("dept_id")))
                .fullDeptId(claims.get("full_dept_id", String.class)).postId(claims.get("post_id", String.class))
                .roleName(claims.get("role_name", String.class)).roleCodes(roleCodes).roleIds(roleIds)
                .detail(detail).build();
    }

    private Long asLong(Object value) {
        return value == null ? null : Long.valueOf(String.valueOf(value));
    }

    private String requireClientCode(Claims claims) {
        String clientCode = claims.get("client_code", String.class);
        if (!StringUtils.hasText(clientCode)) {
            throw new JwtException("客户端编码缺失");
        }
        return clientCode;
    }
}
