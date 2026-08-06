package com.aiems.be.modules.ambulance.socket.controller;

import com.aiems.be.modules.ambulance.service.TransferConfirmService;
import com.aiems.be.modules.ambulance.service.TransferRequestService;
import com.aiems.be.modules.ambulance.socket.payload.TransferConfirmPayload;
import com.aiems.be.modules.ambulance.socket.payload.TransferRequestPayload;
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
public class TransferSocketController {

    private final TransferRequestService transferRequestService;
    private final TransferConfirmService transferConfirmService;

    @MessageMapping("/patient-transfers/requests")
    public void request(@Payload TransferRequestPayload payload, Principal principal) {
        log.info("/patient-transfers/requests payload: {}", payload);
        transferRequestService.request(payload, Long.valueOf(principal.getName()));
    }

    @MessageMapping("/patient-transfers/complete")
    public void confirm(@Payload TransferConfirmPayload payload, Principal principal) {
        log.info("/patient-transfers/complete payload: {}", payload);
        transferConfirmService.confirm(Long.valueOf(principal.getName()), payload.hospitalId());
    }
}
