package com.sample.system.platform.commons.contracts.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StandardResponseTest {

    @Test
    void successResponseCarriesDataAndTrackingId() {
        StandardResponse<String> response = StandardResponse.success("ok", "t-1");
        assertTrue(response.getSuccess());
        assertEquals("ok", response.getData());
        assertEquals("t-1", response.getTrackingId());
        assertNull(response.getErrorDetail());
        assertNotNull(response.getDoTimeStamp());
    }

    @Test
    void errorResponseCarriesMessageCodeAndAction() {
        StandardResponse<Object> response = StandardResponse.error("bad", 2000, "t-2", "retry");
        assertFalse(response.getSuccess());
        assertNull(response.getData());
        assertEquals("bad", response.getErrorDetail().getMessage());
        assertEquals(2000, response.getErrorDetail().getCode());
        assertEquals("retry", response.getErrorDetail().getAction());
    }
}
