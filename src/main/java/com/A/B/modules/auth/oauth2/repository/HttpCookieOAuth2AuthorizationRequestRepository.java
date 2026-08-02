package com.A.B.modules.auth.oauth2.repository;

import com.A.B.common.config.JwtProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

@Component
@ConditionalOnProperty(name = "app.auth.social.enabled", havingValue = "true")
public class HttpCookieOAuth2AuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String COOKIE_NAME = "oauth2_auth_request";
    private static final int COOKIE_MAX_AGE_SECONDS = 180;
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final ObjectMapper objectMapper;
    private final SecretKeySpec hmacKey;

    public HttpCookieOAuth2AuthorizationRequestRepository(ObjectMapper objectMapper, JwtProperties jwtProperties) {
        this.objectMapper = objectMapper;
        this.hmacKey = new SecretKeySpec(jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
    }

    record Payload(
            String authorizationUri,
            String clientId,
            String redirectUri,
            Set<String> scopes,
            String state,
            Map<String, Object> additionalParameters,
            String authorizationRequestUri,
            Map<String, Object> attributes
    ) {
        static Payload from(OAuth2AuthorizationRequest request) {
            return new Payload(
                    request.getAuthorizationUri(),
                    request.getClientId(),
                    request.getRedirectUri(),
                    request.getScopes(),
                    request.getState(),
                    request.getAdditionalParameters(),
                    request.getAuthorizationRequestUri(),
                    request.getAttributes()
            );
        }

        OAuth2AuthorizationRequest toRequest() {
            return OAuth2AuthorizationRequest.authorizationCode()
                    .authorizationUri(authorizationUri)
                    .clientId(clientId)
                    .redirectUri(redirectUri)
                    .scopes(scopes)
                    .state(state)
                    .additionalParameters(params -> params.putAll(additionalParameters))
                    .authorizationRequestUri(authorizationRequestUri)
                    .attributes(attrs -> attrs.putAll(attributes))
                    .build();
        }
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, COOKIE_NAME);
        return cookie != null ? deserialize(cookie.getValue()) : null;
    }

    @Override
    public void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (authorizationRequest == null) {
            deleteCookie(response, request.isSecure());
            return;
        }

        createCookie(response, serialize(authorizationRequest), request.isSecure());
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthorizationRequest authorizationRequest = loadAuthorizationRequest(request);
        deleteCookie(response, request.isSecure());
        return authorizationRequest;
    }

    private void createCookie(HttpServletResponse response, String value, boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, value)
                .path("/")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .maxAge(COOKIE_MAX_AGE_SECONDS)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void deleteCookie(HttpServletResponse response, boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .path("/")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String serialize(OAuth2AuthorizationRequest authorizationRequest) {
        try {
            byte[] json = objectMapper.writeValueAsBytes(Payload.from(authorizationRequest));
            Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
            return encoder.encodeToString(json) + "." + encoder.encodeToString(sign(json));
        } catch (Exception e) {
            throw new IllegalStateException("인가 요청 직렬화에 실패했습니다.", e);
        }
    }

    private OAuth2AuthorizationRequest deserialize(String value) {
        try {
            int separator = value.lastIndexOf('.');
            if (separator < 0) return null;

            byte[] json = Base64.getUrlDecoder().decode(value.substring(0, separator));
            byte[] signature = Base64.getUrlDecoder().decode(value.substring(separator + 1));

            if (!MessageDigest.isEqual(sign(json), signature)) return null;

            return objectMapper.readValue(json, Payload.class).toRequest();
        } catch (Exception e) {
            return null;
        }
    }

    private byte[] sign(byte[] payload) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(hmacKey);
        return mac.doFinal(payload);
    }
}