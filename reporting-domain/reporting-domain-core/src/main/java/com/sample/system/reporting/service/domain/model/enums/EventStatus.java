package com.sample.system.reporting.service.domain.model.enums;

import lombok.Getter;

@Getter
public enum EventStatus {

    RECEIVED,

    PROCESSED,

    FAILED,

    RETRIED
}