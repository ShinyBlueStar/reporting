package com.sample.system.reporting.service.config;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class RequestCorrelationFilterTest {

    private final RequestCorrelationFilter filter = new RequestCorrelationFilter();

    private String run(String headerValue, AtomicReference<String> mdcInside) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (headerValue != null) {
            request.addHeader(RequestCorrelationFilter.HEADER, headerValue);
        }
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                mdcInside.set(MDC.get(RequestCorrelationFilter.MDC_KEY));
            }
        });
        return response.getHeader(RequestCorrelationFilter.HEADER);
    }

    @Test
    void keepsWellFormedIncomingIdAndClearsMdcAfterwards() throws Exception {
        AtomicReference<String> inside = new AtomicReference<>();
        String echoed = run("abc-123", inside);

        assertThat(echoed).isEqualTo("abc-123");
        assertThat(inside.get()).isEqualTo("abc-123");
        assertThat(MDC.get(RequestCorrelationFilter.MDC_KEY)).isNull();
    }

    @Test
    void replacesMissingOrUnsafeIdWithGeneratedOne() throws Exception {
        AtomicReference<String> inside = new AtomicReference<>();
        assertThat(run(null, inside)).isNotBlank().isEqualTo(inside.get());
        assertThat(run("bad id\nforged-log-line", inside)).doesNotContain("\n").isEqualTo(inside.get());
    }
}
