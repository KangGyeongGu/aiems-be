package com.aiems.be.notification.websocket.exception;

import com.aiems.be.common.exception.BusinessException;
import com.aiems.be.common.exception.CommonErrorCode;
import com.aiems.be.contracts.websocket.EventEnvelope;
import com.aiems.be.contracts.websocket.EventType;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Map;

@ControllerAdvice
public class StompExceptionHandler {

    @MessageExceptionHandler(BusinessException.class)
    @SendToUser("/queue/errors")
    public EventEnvelope<Map<String, Object>> handleBusiness(BusinessException ex) {
        Map<String, Object> data = Map.of(
                "code", ex.getErrorCode().getCode(),
                "message", ex.getMessage(),
                "details", ex.getDetails() != null ? ex.getDetails() : Map.of()
        );

        return EventEnvelope.of(EventType.ERROR, data);
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public EventEnvelope<Map<String, Object>> handleGeneric(Exception ex) {
        Map<String, Object> data = Map.of(
                "code", CommonErrorCode.INTERNAL_ERROR.getCode(),
                "message", "처리 중 오류가 발생했습니다.",
                "details", Map.of()
        );

        return EventEnvelope.of(EventType.ERROR, data);
    }
}
