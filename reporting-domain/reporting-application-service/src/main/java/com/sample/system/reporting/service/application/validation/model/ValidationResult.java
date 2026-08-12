package com.sample.system.reporting.service.application.validation.model;

import com.sample.system.reporting.service.domain.model.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

public final class ValidationResult {

    private final List<ValidationError> errors = new ArrayList<>();

    public void add(ValidationError error) {
        if (error != null) {
            errors.add(error);
        }
    }

    public void addAll(List<ValidationError> validationErrors) {
        if (validationErrors != null) {
            errors.addAll(validationErrors);
        }
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<ValidationError> getErrors() {
        return List.copyOf(errors);
    }
}
