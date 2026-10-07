package com.sample.system.reporting.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Resource-server settings. Exactly one token verification source must be configured when security
 * is enabled: a JWK set URI (production, any OIDC provider such as Keycloak) or a shared HMAC secret
 * (local development only).
 */
@ConfigurationProperties(prefix = "reporting.security")
public record SecurityProperties(
        boolean enabled,
        String jwkSetUri,
        String issuerUri,
        String hmacSecret,
        String rolesClaim) {

    public SecurityProperties {
        if (rolesClaim == null || rolesClaim.isBlank()) {
            rolesClaim = "roles";
        }
    }
}
