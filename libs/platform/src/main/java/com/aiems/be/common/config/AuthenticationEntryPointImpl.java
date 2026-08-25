package com.aiems.be.common.config;

import com.aiems.be.common.exception.CommonErrorCode;
import com.aiems.be.common.exception.ErrorResponseWriter;
import com.aiems.be.common.filter.JwtAuthenticationFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    private final ErrorResponseWriter errorResponseWriter;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        boolean expired = request.getAttribute(JwtAuthenticationFilter.EXPIRED_TOKEN_ATTRIBUTE) != null;
        errorResponseWriter.write(response, expired ? CommonErrorCode.TOKEN_EXPIRED : CommonErrorCode.UNAUTHORIZED);
    }
}
