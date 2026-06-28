package com.sample.system.reporting.service.application.exception.handler;

import com.sample.system.reporting.service.application.response.BaseResponse;
import com.sample.system.reporting.service.application.response.ErrorDetail;
import com.sample.system.reporting.service.application.response.ValidationErrorResponse;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.validation.ValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class ReportingGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ReportingDomainException.class)
    public ResponseEntity<BaseResponse<ObjectUtils.Null>> handleReportingDomainException(
            ReportingDomainException exception, WebRequest request) {
        log.error("domain exception in request ==> {} , errorCode ===> {} , message ===> {}",
                ((ServletWebRequest) request).getRequest().getRequestURI(),
                exception.getErrorCode(), exception.getMessage());
        List<ValidationErrorResponse> validationErrors = mapValidationErrors(exception.getValidationErrors());
        ErrorDetail errorDetail = validationErrors.isEmpty()
                ? new ErrorDetail(exception.getMessage(), exception.getErrorCode())
                : new ErrorDetail(exception.getMessage(), exception.getErrorCode(), validationErrors);
        HttpStatus status = validationErrors.isEmpty()
                ? HttpStatus.INTERNAL_SERVER_ERROR
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(new BaseResponse<>(false, errorDetail));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<ObjectUtils.Null>> handleUnexpected(Exception exception, WebRequest request) {
        log.error("unexpected error in request ==> {}",
                ((ServletWebRequest) request).getRequest().getRequestURI(), exception);
        ErrorDetail errorDetail = new ErrorDetail("Internal server error", ErrorCode.REPORT_EXECUTION_FAILED.getCode());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, errorDetail));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<ObjectUtils.Null>> handleConstraintViolation(
            ConstraintViolationException exception, WebRequest request) {
        log.error("constraint violation in request ==> {} , message ===> {}",
                ((ServletWebRequest) request).getRequest().getRequestURI(), exception.getMessage());
        ConstraintViolation<?> violation = exception.getConstraintViolations().iterator().next();
        ErrorDetail errorDetail = new ErrorDetail(
                violation.getMessage(), ErrorCode.INVALID_INPUT_PARAMETER.getCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(false, errorDetail));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  org.springframework.http.HttpHeaders headers,
                                                                  org.springframework.http.HttpStatusCode status,
                                                                  WebRequest request) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : ex.getMessage();
        ErrorDetail errorDetail = new ErrorDetail(message, ErrorCode.INVALID_INPUT_PARAMETER.getCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(false, errorDetail));
    }

    private List<ValidationErrorResponse> mapValidationErrors(List<ValidationError> validationErrors) {
        if (validationErrors == null || validationErrors.isEmpty()) {
            return List.of();
        }
        return validationErrors.stream()
                .map(error -> new ValidationErrorResponse(error.field(), error.code(), error.message()))
                .toList();
    }
}
