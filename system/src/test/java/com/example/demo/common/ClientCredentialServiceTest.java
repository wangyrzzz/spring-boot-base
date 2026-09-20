package com.example.demo.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ClientCredentialServiceTest {
    private PasswordEncoder passwordEncoder;
    private ClientCredentialService service;

    @BeforeEach
    void setUp() {
        passwordEncoder = mock(PasswordEncoder.class);
        service = new ClientCredentialService(passwordEncoder, new ObjectMapper());
    }

    @Test
    void requireActiveRejectsBlankClientCodeBeforeDatabaseAccess() {
        assertThrows(ApiException.class, () -> service.requireActive(" "));
    }

    @Test
    void verifySecretUsesPasswordEncoder() {
        ClientPolicy policy = new ClientPolicy("web", "encoded", "password", 900, 604800, 1, 0);
        doReturn(true).when(passwordEncoder).matches("raw-secret", "encoded");

        service.verifySecret(policy, "raw-secret");

        verify(passwordEncoder).matches("raw-secret", "encoded");
    }

    @Test
    void saveRejectsNewClientWithoutSecret() {
        assertThrows(ApiException.class, () -> service.save(Map.of("clientCode", "web")));
    }
}
