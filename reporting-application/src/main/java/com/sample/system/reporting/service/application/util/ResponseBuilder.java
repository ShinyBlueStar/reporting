package com.sample.system.reporting.service.application.util;

import com.sample.system.platform.commons.contracts.util.MessageDetail;
import com.sample.system.platform.commons.contracts.util.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ResponseBuilder {

    private ResponseBuilder() {
    }

    public static <T> ResponseEntity<StandardResponse<T>> success(T data) {
        StandardResponse<T> response = StandardResponse.<T>builder()
                .success(true)
                .data(data)
                .trackingId(TrackingIdUtil.getTrackingId())
                .doTimeStamp(System.currentTimeMillis())
                .build();
        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<StandardResponse<T>> success(T data, String message) {
        StandardResponse<T> response = StandardResponse.<T>builder()
                .success(true)
                .data(data)
                .trackingId(TrackingIdUtil.getTrackingId())
                .doTimeStamp(System.currentTimeMillis())
                .successDetail(MessageDetail.builder()
                        .message(message)
                        .code(200)
                        .build())
                .build();
        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<StandardResponse<T>> created(T data, String message) {
        StandardResponse<T> response = StandardResponse.<T>builder()
                .success(true)
                .data(data)
                .trackingId(TrackingIdUtil.getTrackingId())
                .doTimeStamp(System.currentTimeMillis())
                .successDetail(MessageDetail.builder()
                        .message(message)
                        .code(200)
                        .build())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
