package com.example.demo;

import com.example.demo.common.AuthProperties;
import com.example.demo.common.AuthenticatedUser;
import com.example.demo.common.JwtTokenService;
import com.example.demo.common.TokenPair;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {
    @Mock
    private StringRedisTemplate redis;
    @Mock
    private ValueOperations<String, String> values;
    @Mock
    private SetOperations<String, String> sets;

    private final Map<String, String> state = new HashMap<>();
    private AuthProperties properties;
    private JwtTokenService service;

    @BeforeEach
    void setUp() {
        properties = new AuthProperties();
        properties.setJwtKey("test-only-change-this-key-to-32-bytes-minimum-123456");
        lenient().when(redis.opsForValue()).thenReturn(values);
        lenient().when(redis.opsForSet()).thenReturn(sets);
        lenient().doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            String member = invocation.getArgument(1);
            state.put(key + ":" + member, member);
            return 1L;
        }).when(sets).add(anyString(), anyString());
        lenient().when(sets.members(anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            return state.keySet().stream().filter(item -> item.startsWith(key + ":"))
                    .map(item -> state.get(item)).collect(java.util.stream.Collectors.toSet());
        });
        doAnswer(invocation -> {
            state.put(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(values).set(anyString(), anyString(), any(Duration.class));
        lenient().when(values.get(anyString())).thenAnswer(invocation -> state.get(invocation.getArgument(0)));
        lenient().doAnswer(invocation -> {
            List<?> keys = invocation.getArgument(1);
            String key = (String) keys.get(0);
            String expected = invocation.getArgument(2);
            if (expected.equals(state.get(key))) {
                state.remove(key);
                return 1L;
            }
            return 0L;
        }).when(redis).execute(any(), anyList(), anyString());
        lenient().doAnswer(invocation -> state.remove(invocation.getArgument(0)) != null)
                .when(redis).delete(anyString());
        service = new JwtTokenService(properties, redis);
    }

    @Test
    void refreshRotationConsumesOldTokenAndKeepsNewTokenUsable() {
        TokenPair first = service.issue(user());
        TokenPair second = service.refresh(first.getRefreshToken());

        assertNotEquals(first.getRefreshToken(), second.getRefreshToken());
        assertEquals(77L, service.validateAccess(second.getAccessToken()).getUserId());
        assertThrows(JwtException.class, () -> service.refresh(first.getRefreshToken()));
    }

    @Test
    void logoutByAccessTokenRevokesTheWholeSession() {
        TokenPair pair = service.issue(user());
        TokenPair rotated = service.refresh(pair.getRefreshToken());

        service.revoke(pair.getAccessToken(), null);

        assertThrows(JwtException.class, () -> service.validateAccess(pair.getAccessToken()));
        assertThrows(JwtException.class, () -> service.validateAccess(rotated.getAccessToken()));
        assertThrows(JwtException.class, () -> service.refresh(rotated.getRefreshToken()));
    }

    @Test
    void multipleSessionsRemainIndependent() {
        TokenPair first = service.issue(user());
        TokenPair second = service.issue(user());

        service.revoke(first.getAccessToken(), null);

        assertThrows(JwtException.class, () -> service.validateAccess(first.getAccessToken()));
        assertEquals(77L, service.validateAccess(second.getAccessToken()).getUserId());
    }

    @Test
    void forgedAndExpiredAccessTokensAreRejected() {
        TokenPair pair = service.issue(user());
        String forged = pair.getAccessToken().substring(0, pair.getAccessToken().length() - 1) + "x";
        assertThrows(JwtException.class, () -> service.validateAccess(forged));

        properties.setAccessTokenTtlSeconds(-1);
        JwtTokenService expiredService = new JwtTokenService(properties, redis);
        TokenPair expired = expiredService.issue(user());
        assertThrows(JwtException.class, () -> expiredService.validateAccess(expired.getAccessToken()));
    }

    private AuthenticatedUser user() {
        return AuthenticatedUser.builder().userId(77L).account("tester").userName("tester")
                .roleIds(List.of(9L)).build();
    }
}
