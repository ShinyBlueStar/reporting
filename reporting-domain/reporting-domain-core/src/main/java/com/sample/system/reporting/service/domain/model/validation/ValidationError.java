package com.sample.system.reporting.service.domain.model.validation;

public record ValidationError(String field, String code, String message) {
}
