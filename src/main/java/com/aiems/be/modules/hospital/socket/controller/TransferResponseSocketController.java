package com.aiems.be.modules.hospital.socket.controller;

import com.aiems.be.modules.hospital.socket.payload.TransferResponsePayload;
import com.aiems.be.modules.transfer.event.TransferResponseMessage;
import com.aiems.be.websocket.event.EventType;
import com.aiems.be.websocket.event.StompEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true")
public class TransferResponseSocketController {

    private static final String TRANSFER_RESPONSE_DESTINATION = "/queue/patient-transfers.responses";

    private final StompEventPublisher stompEventPublisher;

    @MessageMapping("/patient-transfers/responses")
    public void respond(@Payload TransferResponsePayload payload, Principal principal) {
        log.info("/patient-transfers/responses payload: {}", payload);
        Long hospitalId = Long.valueOf(principal.getName());

        stompEventPublisher.sendToUser(
                payload.ambulanceId().toString(),
                TRANSFER_RESPONSE_DESTINATION,
                EventType.TRANSFER_RESPONSE,
                TransferResponseMessage.of(hospitalId, payload.accepted()));
    }
}
