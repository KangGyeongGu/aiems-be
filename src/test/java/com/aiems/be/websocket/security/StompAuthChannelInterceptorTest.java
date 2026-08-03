package com.aiems.be.websocket.security;

import com.aiems.be.common.config.JwtProperties;
import com.aiems.be.common.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@DisplayName("STOMP 채널 인증 인터셉터 단위 테스트")
class StompAuthChannelInterceptorTest {

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
            new JwtProperties("test-secret-key-that-is-at-least-32-bytes-long", Duration.ofMinutes(30), Duration.ofDays(14))
    );

    private final StompAuthChannelInterceptor interceptor = new StompAuthChannelInterceptor(jwtTokenProvider);

    private final MessageChannel channel = mock(MessageChannel.class);

    @Test
    @DisplayName("토큰 없이 연결하면 거부한다")
    void connect_withoutToken_throws() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(MessageDeliveryException.class);
    }

    @Test
    @DisplayName("유효한 토큰으로 연결하면 사용자를 인증한다")
    void connect_withValidToken_setsAuthenticatedUser() {
        String token = jwtTokenProvider.createAccessToken("7", List.of("ROLE_USER"));

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setLeaveMutable(true);
        accessor.setNativeHeader("Authorization", "Bearer " + token);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        interceptor.preSend(message, channel);

        assertThat(accessor.getUser()).isNotNull();
        assertThat(accessor.getUser().getName()).isEqualTo("7");
    }

    @Test
    @DisplayName("인증되지 않은 구독은 거부한다")
    void subscribe_withoutAuthenticatedUser_throws() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(MessageDeliveryException.class);
    }
}
