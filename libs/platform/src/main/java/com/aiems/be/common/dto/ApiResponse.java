package com.aiems.be.common.dto;

public record ApiResponse<T>(
        T data,
        Meta meta
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, Meta.create());
    }
}
