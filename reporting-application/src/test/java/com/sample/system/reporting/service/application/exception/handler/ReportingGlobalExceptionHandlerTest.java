package com.sample.system.reporting.service.application.exception.handler;

import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ReportingGlobalExceptionHandlerTest {

    private final ReportingGlobalExceptionHandler handler = new ReportingGlobalExceptionHandler();
    private final ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest("POST", "/x"));

    @Test
    void unexpectedExceptionIsMappedTo500WithoutLeakingMessage() {
        var response = handler.handleUnexpected(new IllegalStateException("jdbc:secret-detail"), request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(String.valueOf(response.getBody()).contains("secret-detail"));
    }

    @Test
    void domainExceptionWithoutValidationErrorsIsMappedTo500() {
        var response = handler.handleReportingDomainException(new ReportingDomainException("1", "boom"), request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
