package com.aiems.be.common.exception;

import com.aiems.be.common.dto.ErrorDetail;
import com.aiems.be.common.dto.ErrorResponse;
import com.aiems.be.common.filter.RequestIdFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public void write(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        String requestId = MDC.get(RequestIdFilter.MDC_KEY);

        ErrorDetail errorDetail = ErrorDetail.of(errorCode.getCode(), errorCode.getDefaultMessage());
        ErrorResponse errorResponse = ErrorResponse.of(errorDetail, requestId);

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
