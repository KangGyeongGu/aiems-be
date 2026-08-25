package com.aiems.be.common.filter;

import com.aiems.be.common.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String EXPIRED_TOKEN_ATTRIBUTE = JwtAuthenticationFilter.class.getName() + ".EXPIRED";

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = extractToken(request);

        if (token != null) {
            try {
                authenticate(jwtTokenProvider.parse(token));
            } catch (ExpiredJwtException ex) {
                request.setAttribute(EXPIRED_TOKEN_ATTRIBUTE, Boolean.TRUE);
                log.debug("Expired JWT token: {}", ex.getMessage());
            } catch (JwtException | IllegalArgumentException ex) {
                log.debug("Invalid JWT token: {}", ex.getMessage());
            } catch (Exception e) {
                log.error("Failed to authenticate user", e);
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private void authenticate(Claims claims) {
        String subject = jwtTokenProvider.getSubject(claims);

        List<SimpleGrantedAuthority> authorities = jwtTokenProvider.getRoles(claims).stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(subject, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("Authenticated user: subject={}", subject);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
