package com.aiems.be.notification.websocket.security;

import com.aiems.be.common.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_DESTINATION_PREFIX = "/user/";

    private final JwtTokenProvider jwtTokenProvider;

    @Nullable
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) return message;

        switch (accessor.getCommand()) {
            case CONNECT -> handleConnect(accessor);
            case SUBSCRIBE -> authorizeSubscribe(accessor);
            case SEND -> throw new MessageDeliveryException("허용되지 않은 전송입니다.");
            default -> {}
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            throw new MessageDeliveryException("인증이 필요합니다.");
        }

        String token = header.substring(BEARER_PREFIX.length());
        Claims claims;
        try {
            claims = jwtTokenProvider.parse(token);
        } catch (ExpiredJwtException ex) {
            throw new MessageDeliveryException("만료된 토큰입니다.");
        } catch (JwtException | IllegalArgumentException ex) {
            throw new MessageDeliveryException("유효하지 않은 토큰입니다.");
        }

        String subject = jwtTokenProvider.getSubject(claims);
        List<SimpleGrantedAuthority> authorities = jwtTokenProvider.getRoles(claims).stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        Authentication authentication = new UsernamePasswordAuthenticationToken(subject, null, authorities);
        accessor.setUser(authentication);
    }

    private void authorizeSubscribe(StompHeaderAccessor accessor) {
        if (accessor.getUser() == null) {
            throw new MessageDeliveryException("인증이 필요합니다.");
        }

        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith(USER_DESTINATION_PREFIX)) {
            throw new MessageDeliveryException("허용되지 않은 구독입니다.");
        }
    }

}
