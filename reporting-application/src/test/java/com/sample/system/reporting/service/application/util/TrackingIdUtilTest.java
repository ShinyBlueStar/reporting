package com.sample.system.reporting.service.application.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrackingIdUtilTest {

    @AfterEach
    void cleanUp() {
        TrackingIdUtil.clearTrackingId();
    }

    @Test
    void generatesAndReusesIdWithinTheSameThread() {
        String first = TrackingIdUtil.getTrackingId();
        assertNotNull(first);
        assertEquals(first, TrackingIdUtil.getTrackingId());
    }

    @Test
    void clearRemovesTheStoredId() {
        TrackingIdUtil.setTrackingId("fixed");
        assertEquals("fixed", TrackingIdUtil.getTrackingId());
        TrackingIdUtil.clearTrackingId();
        assertNotEquals("fixed", TrackingIdUtil.getTrackingId());
    }
}
