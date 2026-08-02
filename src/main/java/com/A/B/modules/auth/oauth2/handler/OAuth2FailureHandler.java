package com.A.B.modules.auth.oauth2.handler;

import com.A.B.modules.auth.oauth2.config.OAuth2Properties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.auth.social.enabled", havingValue = "true")
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final OAuth2Properties oAuth2Properties;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        getRedirectStrategy().sendRedirect(request, response, oAuth2Properties.failureRedirectUri());
    }
}
