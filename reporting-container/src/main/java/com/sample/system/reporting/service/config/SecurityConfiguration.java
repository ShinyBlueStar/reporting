package com.sample.system.reporting.service.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.core.convert.converter.Converter;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Stateless JWT resource server.
 * <ul>
 *   <li>{@code REPORT_USER}: run reports, download results, read definitions/templates/rules.</li>
 *   <li>{@code REPORT_ADMIN}: everything, including create/update/delete of definitions, parameters,
 *       templates and validation rules (definitions carry SQL, so write access is privileged).</li>
 * </ul>
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfiguration {

    public static final String ROLE_USER = "REPORT_USER";
    public static final String ROLE_ADMIN = "REPORT_ADMIN";

    private static final String[] PUBLIC_PATHS = {
            "/actuator/health/**", "/actuator/info", "/actuator/prometheus",
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/", "/index.html", "/favicon.ico"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityProperties properties) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        if (!properties.enabled()) {
            http.authorizeHttpRequests(a -> a.anyRequest().permitAll());
            return http.build();
        }
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_PATHS).permitAll()
                .requestMatchers("/api/v1/report-execution/**")
                        .hasAnyRole(ROLE_USER, ROLE_ADMIN)
                .requestMatchers(HttpMethod.POST, "/api/v1/*/find", "/api/v1/*/search")
                        .hasAnyRole(ROLE_USER, ROLE_ADMIN)
                .requestMatchers("/api/**").hasRole(ROLE_ADMIN)
                .anyRequest().denyAll())
                .oauth2ResourceServer(o -> o.jwt(jwt -> jwt.jwtAuthenticationConverter(
                        jwtAuthenticationConverter(properties.rolesClaim()))));
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder(SecurityProperties properties) {
        if (!properties.enabled()) {
            return token -> {
                throw new IllegalStateException("Security is disabled");
            };
        }
        NimbusJwtDecoder decoder;
        if (properties.jwkSetUri() != null && !properties.jwkSetUri().isBlank()) {
            decoder = NimbusJwtDecoder.withJwkSetUri(properties.jwkSetUri()).build();
        } else if (properties.hmacSecret() != null && properties.hmacSecret().length() >= 32) {
            decoder = NimbusJwtDecoder.withSecretKey(new SecretKeySpec(
                    properties.hmacSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"))
                    .macAlgorithm(MacAlgorithm.HS256).build();
        } else {
            throw new IllegalStateException("reporting.security.enabled=true requires reporting.security.jwk-set-uri "
                    + "or reporting.security.hmac-secret (at least 32 characters)");
        }
        if (properties.issuerUri() != null && !properties.issuerUri().isBlank()) {
            decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(properties.issuerUri()));
        }
        return decoder;
    }

    static JwtAuthenticationConverter jwtAuthenticationConverter(String rolesClaim) {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> extractRoles(jwt, rolesClaim));
        return converter;
    }

    /** Reads roles from the configured claim (list or space separated) and Keycloak's realm_access.roles. */
    @SuppressWarnings("unchecked")
    static Collection<GrantedAuthority> extractRoles(Jwt jwt, String rolesClaim) {
        java.util.Set<String> roles = new java.util.LinkedHashSet<>();
        Object claim = jwt.getClaims().get(rolesClaim);
        if (claim instanceof Collection<?> c) {
            c.forEach(r -> roles.add(String.valueOf(r)));
        } else if (claim instanceof String s && !s.isBlank()) {
            roles.addAll(List.of(s.trim().split("\\s+")));
        }
        Object realmAccess = jwt.getClaims().get("realm_access");
        if (realmAccess instanceof Map<?, ?> m && m.get("roles") instanceof Collection<?> rr) {
            rr.forEach(r -> roles.add(String.valueOf(r)));
        }
        return roles.stream()
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .<GrantedAuthority>map(SimpleGrantedAuthority::new)
                .toList();
    }
}
