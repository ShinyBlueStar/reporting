package com.sample.system.reporting.service.application.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetail {

    private String message;
    private String code;
    private List<ValidationErrorResponse> validationErrors;

    public ErrorDetail(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public ErrorDetail(String message, String code, List<ValidationErrorResponse> validationErrors) {
        this.message = message;
        this.code = code;
        this.validationErrors = validationErrors;
    }
}
