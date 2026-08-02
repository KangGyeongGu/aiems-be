package com.A.B.common.dto;

public record ApiResponse<T>(
        T data,
        Meta meta
) {
    public static <T> ApiResponse<T> success(T data, String requestId) {
        return new ApiResponse<>(data, Meta.create(requestId));
    }
}
