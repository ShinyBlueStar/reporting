package com.sample.system.reporting.service.application.util;

import java.util.UUID;

public final class TrackingIdUtil {

    private static final ThreadLocal<String> TRACKING_ID = new ThreadLocal<>();

    private TrackingIdUtil() {
    }

    public static String generateTrackingId() {
        return UUID.randomUUID().toString();
    }

    public static void setTrackingId(String trackingId) {
        TRACKING_ID.set(trackingId);
    }

    public static String getTrackingId() {
        String trackingId = TRACKING_ID.get();
        if (trackingId == null) {
            trackingId = generateTrackingId();
            setTrackingId(trackingId);
        }
        return trackingId;
    }

    public static void clearTrackingId() {
        TRACKING_ID.remove();
    }
}
