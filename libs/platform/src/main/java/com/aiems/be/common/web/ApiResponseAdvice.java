package com.aiems.be.common.web;

import com.aiems.be.common.dto.ApiResponse;
import com.aiems.be.common.dto.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.aiems.be")
@RequiredArgsConstructor
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> parameterType = returnType.getParameterType();
        return !ApiResponse.class.isAssignableFrom(parameterType) && !ErrorResponse.class.isAssignableFrom(parameterType);
    }

    @Nullable
    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (body == null || body instanceof byte[] || body instanceof Resource) {
            return body;
        }

        if (body instanceof ApiResponse<?> || body instanceof ErrorResponse) {
            return body;
        }

        if (request.getURI().getPath().startsWith("/internal")) {
            return body;
        }

        if (selectedContentType.isCompatibleWith(MediaType.APPLICATION_XML)) {
            return body;
        }

        ApiResponse<Object> wrapped = ApiResponse.success(body);

        if (body instanceof String) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return writeAsString(wrapped);
        }

        return wrapped;
    }

    private String writeAsString(ApiResponse<Object> wrapped) {
        try {
            return objectMapper.writeValueAsString(wrapped);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("응답 직렬화에 실패했습니다.", e);
        }
    }
}
