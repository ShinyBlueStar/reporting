package com.sample.system.reporting.service.application.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse<T> {

    private Boolean success;
    private T data;
    private String trackingId;
    private Long doTimeStamp;
    private ErrorDetail errorDetail;

    public BaseResponse() {
    }

    public BaseResponse(Boolean success, T data) {
        this.success = success;
        this.data = data;
        this.doTimeStamp = System.currentTimeMillis();
    }

    public BaseResponse(Boolean success, ErrorDetail errorDetail) {
        this.success = success;
        this.errorDetail = errorDetail;
        this.doTimeStamp = System.currentTimeMillis();
    }
}
