package com.aiems.be.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorResponse(
        @JsonProperty("error")
        ErrorDetail errorDetail,

        Meta meta
) {
    public static ErrorResponse of(ErrorDetail errorDetail, String requestId) {
        return new ErrorResponse(errorDetail, Meta.create(requestId));
    }
}
