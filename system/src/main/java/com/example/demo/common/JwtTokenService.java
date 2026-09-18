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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class JwtTokenService {
    private static final String ACCESS_PREFIX = "auth:access:";
    private static final String REFRESH_PREFIX = "auth:refresh:";
    private static final String SESSION_REFRESH_PREFIX = "auth:session:refresh:";
    private static final String SESSION_ACCESS_PREFIX = "auth:session:access:";
    private static final DefaultRedisScript<Long> CONSUME_REFRESH = new DefaultRedisScript<>(
            "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end", Long.class);

    private final AuthProperties properties;
    private final StringRedisTemplate redis;
    private final SecretKey signingKey;
    private final ObjectProvider<ClientCredentialService> clientCredentialService;

    public JwtTokenService(AuthProperties properties, StringRedisTemplate redis) {
        this(properties, redis, null);
    }

    @Autowired
    public JwtTokenService(AuthProperties properties, StringRedisTemplate redis,
                           ObjectProvider<ClientCredentialService> clientCredentialService) {
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
        return issue(user, user == null ? null : user.getClientId(),
                properties.getAccessTokenTtlSeconds(), properties.getRefreshTokenTtlSeconds());
    }

    public TokenPair issue(AuthenticatedUser user, String clientId, long accessTtlSeconds, long refreshTtlSeconds) {
        return issue(user, clientId, accessTtlSeconds, refreshTtlSeconds, null);
    }

    private TokenPair issue(AuthenticatedUser user, String clientId, long accessTtlSeconds, long refreshTtlSeconds,
                            String existingSessionId) {
        String accessJti = UUID.randomUUID().toString();
        String refreshJti = UUID.randomUUID().toString();
        String sessionId = StringUtils.hasText(existingSessionId) ? existingSessionId : UUID.randomUUID().toString();
        Date now = new Date();
        Date accessExpiry = new Date(now.getTime() + accessTtlSeconds * 1000);
        Date refreshExpiry = new Date(now.getTime() + refreshTtlSeconds * 1000);
        String access = buildToken(user, clientId, accessJti, sessionId, "access", now, accessExpiry);
        String refresh = buildToken(user, clientId, refreshJti, sessionId, "refresh", now, refreshExpiry);
        if (properties.isRedisState()) {
            redis.opsForValue().set(ACCESS_PREFIX + accessJti, access, Duration.ofSeconds(accessTtlSeconds));
            redis.opsForValue().set(REFRESH_PREFIX + refreshJti, refresh, Duration.ofSeconds(refreshTtlSeconds));
            redis.opsForValue().set(SESSION_REFRESH_PREFIX + sessionId, refreshJti,
                    Duration.ofSeconds(refreshTtlSeconds));
            redis.opsForSet().add(SESSION_ACCESS_PREFIX + sessionId, accessJti);
            redis.expire(SESSION_ACCESS_PREFIX + sessionId, refreshTtlSeconds, TimeUnit.SECONDS);
        }
        return new TokenPair(access, refresh, "Bearer", accessTtlSeconds, refreshTtlSeconds);
    }

    public AuthenticatedUser validateAccess(String token) {
        Claims claims = parse(token);
        if (!"access".equals(claims.get("token_type", String.class))) {
            throw new JwtException("not an access token");
        }
        if (properties.isRedisState() && !token.equals(redis.opsForValue().get(ACCESS_PREFIX + claims.getId()))) {
            throw new JwtException("access token revoked");
        }
        return toUser(claims);
    }

    public TokenPair refresh(String token) {
        Claims claims = parse(token);
        if (!"refresh".equals(claims.get("token_type", String.class))) {
            throw new JwtException("not a refresh token");
        }
        if (properties.isRedisState()) {
            Long consumed = redis.execute(CONSUME_REFRESH,
                    Collections.singletonList(REFRESH_PREFIX + claims.getId()), token);
            if (!Long.valueOf(1L).equals(consumed)) {
                throw new JwtException("refresh token already used or revoked");
            }
        }
        String clientId = claims.get("client_id", String.class);
        ClientPolicy policy = resolvePolicy(clientId);
        return issue(toUser(claims), clientId, policy.accessTokenValidity(), policy.refreshTokenValidity(),
                claims.get("session_id", String.class));
    }

    private ClientPolicy resolvePolicy(String clientId) {
        if (!StringUtils.hasText(clientId) || clientCredentialService == null) {
            return defaultPolicy(clientId);
        }
        ClientCredentialService service = clientCredentialService.getIfAvailable();
        return service == null ? defaultPolicy(clientId) : service.requireActive(clientId);
    }

    private ClientPolicy defaultPolicy(String clientId) {
        return new ClientPolicy(clientId, null, "password", properties.getAccessTokenTtlSeconds(),
                properties.getRefreshTokenTtlSeconds(), 1, 0);
    }

    public void revoke(String accessToken, String refreshToken) {
        if (!properties.isRedisState()) {
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
            Set<String> accessJtis = redis.opsForSet().members(SESSION_ACCESS_PREFIX + sessionId);
            if (accessJtis != null) {
                accessJtis.forEach(jti -> redis.delete(ACCESS_PREFIX + jti));
            }
            redis.delete(SESSION_ACCESS_PREFIX + sessionId);
            redis.delete(SESSION_REFRESH_PREFIX + sessionId);
        }
        deleteTokenKey(accessToken, ACCESS_PREFIX);
        deleteTokenKey(refreshToken, REFRESH_PREFIX);
    }

    public String bearerToken(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        return authorization.regionMatches(true, 0, "Bearer ", 0, 7)
                ? authorization.substring(7).trim() : authorization.trim();
    }

    private void deleteTokenKey(String token, String prefix) {
        try {
            Claims claims = parse(token);
            redis.delete(prefix + claims.getId());
        } catch (RuntimeException ignored) {
            // Logout remains idempotent for expired or malformed tokens.
        }
    }

    private String buildToken(AuthenticatedUser user, String clientId, String jti, String sessionId, String type,
                              Date issuedAt, Date expiry) {
        var builder = Jwts.builder().id(jti).subject(String.valueOf(user.getUserId()))
                .claim("session_id", sessionId).claim("token_type", type).claim("client_id", clientId)
                .claim("user_id", user.getUserId()).claim("account", user.getAccount())
                .claim("user_name", user.getUserName()).claim("nick_name", user.getNickName())
                .claim("tenant_id", user.getTenantId()).claim("dept_id", user.getDeptId())
                .claim("full_dept_id", user.getFullDeptId()).claim("post_id", user.getPostId())
                .claim("role_name", user.getRoleName()).claim("role_ids", user.getRoleIds())
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
        Map<String, Object> detail = new HashMap<>();
        Object rawDetail = claims.get("detail");
        if (rawDetail instanceof Map<?, ?> map) {
            map.forEach((key, value) -> detail.put(String.valueOf(key), value));
        }
        return AuthenticatedUser.builder().userId(asLong(claims.get("user_id")))
                .clientId(claims.get("client_id", String.class)).account(claims.get("account", String.class))
                .userName(claims.get("user_name", String.class)).nickName(claims.get("nick_name", String.class))
                .tenantId(asLong(claims.get("tenant_id"))).deptId(asLong(claims.get("dept_id")))
                .fullDeptId(claims.get("full_dept_id", String.class)).postId(claims.get("post_id", String.class))
                .roleName(claims.get("role_name", String.class)).roleIds(roleIds).detail(detail).build();
    }

    private Long asLong(Object value) {
        return value == null ? null : Long.valueOf(String.valueOf(value));
    }
}
