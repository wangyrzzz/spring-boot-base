package com.example.demo.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ClientCredentialServiceTest {
    private JdbcTemplate jdbcTemplate;
    private PasswordEncoder passwordEncoder;
    private ClientCredentialService service;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new ClientCredentialService(jdbcTemplate, passwordEncoder);
    }

    @Test
    void requireActiveUsesClientCodeColumn() {
        ClientPolicy policy = new ClientPolicy("web", "encoded", "password", 900, 604800, 1, 0);
        doReturn(policy).when(jdbcTemplate).queryForObject(
                anyString(), any(RowMapper.class), any(Object[].class));

        assertEquals(policy, service.requireActive("web"));

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForObject(sql.capture(), any(RowMapper.class), any(Object[].class));
        assertTrue(sql.getValue().contains("client_code"));
        assertFalse(sql.getValue().contains("client_id"));
    }

    @Test
    void listAndDetailExposeCurrentColumnAliases() {
        doReturn(java.util.List.of()).when(jdbcTemplate).queryForList(anyString());
        doReturn(Map.of()).when(jdbcTemplate).queryForMap(anyString(), any(Object[].class));

        service.list();
        service.detail(7L);

        ArgumentCaptor<String> listSql = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForList(listSql.capture());
        assertTrue(listSql.getValue().contains("client_code as clientCode"));
        assertTrue(listSql.getValue().contains("auto_approve as autoApprove"));
        assertFalse(listSql.getValue().contains("client_id"));
        assertFalse(listSql.getValue().contains("autoapprove"));

        ArgumentCaptor<String> detailSql = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForMap(detailSql.capture(), any(Object[].class));
        assertTrue(detailSql.getValue().contains("client_code as clientCode"));
        assertTrue(detailSql.getValue().contains("auto_approve as autoApprove"));
        assertFalse(detailSql.getValue().contains("client_id"));
        assertFalse(detailSql.getValue().contains("autoapprove"));
    }

    @Test
    void saveInsertUsesClientCodeAndAutoApprove() {
        doReturn("encoded-secret").when(passwordEncoder).encode("raw-secret");
        doReturn(11L).when(jdbcTemplate).queryForObject("select last_insert_id()", Long.class);
        doReturn(1).when(jdbcTemplate).update(anyString(), any(Object[].class));

        Map<String, Object> input = clientInput(null);
        input.put("clientSecret", "raw-secret");
        input.put("autoApprove", "true");

        assertEquals(11L, service.save(input));

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).update(sql.capture(), any(Object[].class));
        assertTrue(sql.getValue().contains("client_code"));
        assertTrue(sql.getValue().contains("auto_approve"));
        assertFalse(sql.getValue().contains("client_id"));
        assertFalse(sql.getValue().contains("autoapprove"));
    }

    @Test
    void saveUpdateWithAndWithoutSecretUsesAutoApprove() {
        doReturn("encoded-secret").when(passwordEncoder).encode("raw-secret");
        doReturn(1).when(jdbcTemplate).update(anyString(), any(Object[].class));

        Map<String, Object> withSecret = clientInput(12L);
        withSecret.put("clientSecret", "raw-secret");
        withSecret.put("autoApprove", "true");
        service.save(withSecret);

        Map<String, Object> withoutSecret = clientInput(13L);
        withoutSecret.put("autoApprove", "false");
        service.save(withoutSecret);

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate, org.mockito.Mockito.times(2)).update(sql.capture(), any(Object[].class));
        for (String statement : sql.getAllValues()) {
            assertTrue(statement.contains("client_code"));
            assertTrue(statement.contains("auto_approve"));
            assertFalse(statement.contains("client_id"));
            assertFalse(statement.contains("autoapprove"));
        }
    }

    @Test
    void removeSoftDeletesClient() {
        doReturn(1).when(jdbcTemplate).update(anyString(), any(Object[].class));

        service.remove(21L);

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).update(sql.capture(), any(Object[].class));
        assertEquals("update sys_client set deleted=1,status=0,update_time=current_timestamp where id=?", sql.getValue());
        verifyNoMoreInteractions(passwordEncoder);
    }

    private Map<String, Object> clientInput(Long id) {
        Map<String, Object> input = new HashMap<>();
        input.put("id", id);
        input.put("clientCode", "web");
        input.put("resourceIds", "resource");
        input.put("scope", "*");
        input.put("authorizedGrantTypes", "password,refresh_token");
        input.put("webServerRedirectUri", null);
        input.put("authorities", null);
        input.put("accessTokenValidity", 900);
        input.put("refreshTokenValidity", 604800);
        input.put("additionalInformation", null);
        input.put("status", 1);
        return input;
    }
}
