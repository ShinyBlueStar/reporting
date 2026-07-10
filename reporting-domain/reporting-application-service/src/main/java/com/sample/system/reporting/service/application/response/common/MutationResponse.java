package com.sample.system.reporting.service.application.response.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MutationResponse {
    private final Long id;
    private final String message;
}
