package com.example.demo.common;

/** Effective client policy used for one token issuance. */
public record ClientPolicy(String clientCode, String clientSecret, String grantTypes,
                           long accessTokenValidity, long refreshTokenValidity,
                           int status, int deleted) {
    public boolean active() {
        return status == 1 && deleted == 0;
    }

    public boolean allows(String grantType) {
        if (grantTypes == null || grantTypes.isBlank()) {
            return false;
        }
        return java.util.Arrays.stream(grantTypes.split(",|\\s+"))
                .map(String::trim).anyMatch(grantType::equalsIgnoreCase);
    }
}
