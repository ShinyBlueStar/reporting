package com.sample.system.reporting.service.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityConfigurationTest {

    private Jwt jwt(Map<String, Object> claims) {
        return new Jwt("t", Instant.now(), Instant.now().plusSeconds(60), Map.of("alg", "HS256"), claims);
    }

    @Test
    void mapsRolesFromConfiguredClaimAndKeycloakRealmAccess() {
        Jwt token = jwt(Map.of("sub", "u1", "roles", List.of("REPORT_USER"),
                "realm_access", Map.of("roles", List.of("ROLE_REPORT_ADMIN"))));
        assertThat(SecurityConfiguration.extractRoles(token, "roles").stream().map(GrantedAuthority::getAuthority))
                .containsExactlyInAnyOrder("ROLE_REPORT_USER", "ROLE_REPORT_ADMIN");
    }

    @Test
    void supportsSpaceSeparatedClaimAndNoRoles() {
        assertThat(SecurityConfiguration.extractRoles(jwt(Map.of("sub", "u", "scope", "A B")), "scope"))
                .hasSize(2);
        assertThat(SecurityConfiguration.extractRoles(jwt(Map.of("sub", "u")), "roles")).isEmpty();
    }

    @Test
    void enabledSecurityWithoutKeySourceFailsFast() {
        SecurityProperties props = new SecurityProperties(true, "", "", "short", null);
        assertThatThrownBy(() -> new SecurityConfiguration().jwtDecoder(props))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void hmacSecretDecoderIsCreated() {
        SecurityProperties props = new SecurityProperties(true, null, null, "0123456789abcdef0123456789abcdef", null);
        assertThat(new SecurityConfiguration().jwtDecoder(props)).isNotNull();
    }
}
