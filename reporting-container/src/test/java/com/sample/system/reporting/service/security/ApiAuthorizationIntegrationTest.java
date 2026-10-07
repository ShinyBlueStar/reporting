package com.sample.system.reporting.service.security;

import com.sample.system.reporting.service.ReportingServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@DirtiesContext
@SpringBootTest(classes = ReportingServiceApplication.class, properties = {
        "reporting.security.enabled=true",
        "reporting.security.hmac-secret=0123456789abcdef0123456789abcdef"
})
class ApiAuthorizationIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder search(String path) {
        return post(path).contentType(MediaType.APPLICATION_JSON).content("{}");
    }

    @Test
    void anonymousRequestsAreRejected() throws Exception {
        mvc.perform(search("/api/v1/report-definition/search")).andExpect(status().isUnauthorized());
    }

    @Test
    void userCannotWriteDefinitions() throws Exception {
        mvc.perform(search("/api/v1/report-definition/update")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_REPORT_USER"))))
                .andExpect(status().isForbidden());
        mvc.perform(search("/api/v1/report-definition")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_REPORT_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void userCanReachReadEndpoints() throws Exception {
        // authorized => request reaches the controller (validation may fail with 400, but never 401/403)
        int code = mvc.perform(search("/api/v1/report-definition/search")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_REPORT_USER"))))
                .andReturn().getResponse().getStatus();
        org.junit.jupiter.api.Assertions.assertNotEquals(401, code);
        org.junit.jupiter.api.Assertions.assertNotEquals(403, code);
    }

    @Test
    void healthIsPublic() throws Exception {
        // may be 503 when a dependency (e.g. RabbitMQ) is down in the test environment, but never 401/403
        int code = mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/actuator/health"))
                .andReturn().getResponse().getStatus();
        org.junit.jupiter.api.Assertions.assertTrue(code == 200 || code == 503, "status " + code);
    }
}
