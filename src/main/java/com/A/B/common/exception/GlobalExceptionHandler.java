package com.A.B.common.exception;

import com.A.B.common.dto.ErrorDetail;
import com.A.B.common.dto.ErrorResponse;
import com.A.B.common.filter.RequestIdFilter;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        String requestId = MDC.get(RequestIdFilter.MDC_KEY);
        ErrorCode errorCode = ex.getErrorCode();

        log.warn("Business Exception: code={}, message={}, requestId={}", errorCode.getCode(), ex.getMessage(), requestId);

        ErrorDetail errorDetail = ErrorDetail.of(errorCode.getCode(), ex.getMessage(), ex.getDetails());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorDetail, requestId));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<ErrorDetail.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldError)
                .toList();

        return fieldErrorResponse(fieldErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        List<ErrorDetail.FieldError> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(this::toFieldError)
                .toList();

        return fieldErrorResponse(fieldErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        return respond(CommonErrorCode.MALFORMED_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        return respond(CommonErrorCode.VALIDATION_FAILED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return respond(CommonErrorCode.VALIDATION_FAILED);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return respond(CommonErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        return respond(CommonErrorCode.NOT_FOUND);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return respond(CommonErrorCode.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.warn("Access denied: message={}, requestId={}", ex.getMessage(), MDC.get(RequestIdFilter.MDC_KEY));
        return respond(CommonErrorCode.ACCESS_DENIED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex) {
        log.error("Unexpected exception: requestId={}", MDC.get(RequestIdFilter.MDC_KEY), ex);
        return respond(CommonErrorCode.INTERNAL_ERROR);
    }

    private ResponseEntity<ErrorResponse> respond(ErrorCode errorCode) {
        String requestId = MDC.get(RequestIdFilter.MDC_KEY);
        ErrorDetail errorDetail = ErrorDetail.of(errorCode.getCode(), errorCode.getDefaultMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorDetail, requestId));
    }

    private ResponseEntity<ErrorResponse> fieldErrorResponse(List<ErrorDetail.FieldError> fieldErrors) {
        String requestId = MDC.get(RequestIdFilter.MDC_KEY);

        ErrorDetail errorDetail = ErrorDetail.withFieldErrors(
                CommonErrorCode.VALIDATION_FAILED.getCode(),
                CommonErrorCode.VALIDATION_FAILED.getDefaultMessage(),
                fieldErrors
        );

        return ResponseEntity
                .status(CommonErrorCode.VALIDATION_FAILED.getHttpStatus())
                .body(ErrorResponse.of(errorDetail, requestId));
    }

    private ErrorDetail.FieldError toFieldError(FieldError fieldError) {
        String field = fieldError.getField();
        String reason = fieldError.getDefaultMessage() != null
                ? fieldError.getDefaultMessage()
                : "invalid";

        return new ErrorDetail.FieldError(field, reason);
    }

    private ErrorDetail.FieldError toFieldError(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        String field = path.contains(".") ? path.substring(path.lastIndexOf(".") + 1) : path;
        String reason = violation.getMessage();

        return new ErrorDetail.FieldError(field, reason);
    }
}
