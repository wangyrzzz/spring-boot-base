package com.example.demo.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientPolicyTest {
    @Test
    void grantTypesAndStatusAreEnforced() {
        ClientPolicy policy = new ClientPolicy("web", "secret", "password, refresh_token", 10, 20, 1, 0);
        assertTrue(policy.active());
        assertTrue(policy.allows("password"));
        assertFalse(policy.allows("client_credentials"));
        assertFalse(new ClientPolicy("web", "secret", "password", 10, 20, 0, 0).active());
    }
}
