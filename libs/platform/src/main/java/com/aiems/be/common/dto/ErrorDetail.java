package com.aiems.be.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDetail(
        String code,
        String message,
        Map<String, Object> details
) {
    public record FieldError(String field, String reason) {}

    public static ErrorDetail of(String code, String message) {
        return new ErrorDetail(code, message, null);
    }

    public static ErrorDetail of(String code, String message, Map<String, Object> details) {
        return new ErrorDetail(code, message, details);
    }

    public static ErrorDetail withFieldErrors(String code, String message, List<FieldError> fieldErrors) {
        return new ErrorDetail(code, message, Map.of("fieldErrors", fieldErrors));
    }
}
