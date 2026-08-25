package com.aiems.be.notification.websocket.exception;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Component
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    @Nullable
    @Override
    public Message<byte[]> handleClientMessageProcessingError(@Nullable Message<byte[]> clientMessage, Throwable ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        String message = cause.getMessage() != null ? cause.getMessage() : "요청을 처리할 수 없습니다.";

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(message);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }
}
