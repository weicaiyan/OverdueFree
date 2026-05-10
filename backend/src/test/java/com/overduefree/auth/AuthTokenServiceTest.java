package com.overduefree.auth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthTokenServiceTest {

    private final AuthTokenService authTokenService = new AuthTokenService();

    @Test
    void shouldGenerateDifferentTokensAndStableHash() {
        String firstToken = authTokenService.generateToken();
        String secondToken = authTokenService.generateToken();

        assertThat(firstToken).isNotBlank();
        assertThat(secondToken).isNotBlank();
        assertThat(firstToken).isNotEqualTo(secondToken);
        assertThat(authTokenService.hashToken(firstToken)).hasSize(64);
        assertThat(authTokenService.hashToken(firstToken)).isEqualTo(authTokenService.hashToken(firstToken));
    }

    @Test
    void shouldResolveBearerToken() {
        assertThat(authTokenService.resolveBearerToken("Bearer abc")).isEqualTo("abc");
        assertThat(authTokenService.resolveBearerToken("abc")).isNull();
    }
}
