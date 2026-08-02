package com.A.B.common.security;

import com.A.B.common.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private static final String ROLES_CLAIM = "roles";
    private static final long CLOCK_SKEW_SECONDS = 30;

    private final SecretKey secretKey;
    private final JwtParser jwtParser;
    private final long accessValidityMillis;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8)
        );

        this.jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .clockSkewSeconds(CLOCK_SKEW_SECONDS)
                .build();

        this.accessValidityMillis = jwtProperties.accessExpiration().toMillis();
    }

    public String createAccessToken(String subject, List<String> roles) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessValidityMillis);

        return Jwts.builder()
                .subject(subject)
                .claim(ROLES_CLAIM, roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public Claims parse(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }

    public String getSubject(Claims claims) {
        return claims.getSubject();
    }

    public List<String> getRoles(Claims claims) {
        Object roles = claims.get(ROLES_CLAIM);
        if (roles instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return Collections.emptyList();
    }
}
