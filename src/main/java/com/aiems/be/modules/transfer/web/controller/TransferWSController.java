package com.aiems.be.modules.transfer.web.controller;

import com.aiems.be.modules.transfer.service.SendTransferCompleteService;
import com.aiems.be.modules.transfer.service.SendTransferRequestService;
import com.aiems.be.modules.transfer.service.command.CreateTransferCompleteMessageCommand;
import com.aiems.be.modules.transfer.web.message.PatientTransferCompleteMessage;
import com.aiems.be.modules.transfer.web.message.PatientTransferResponseMessage;
import com.aiems.be.modules.transfer.web.payload.PatientTransferCompletePayload;
import com.aiems.be.modules.transfer.web.payload.PatientTransferRequestPayload;
import com.aiems.be.modules.transfer.web.payload.PatientTransferResponsePayload;
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
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true")
@Controller
public class TransferWSController {

    private static final String TRANSFER_RESPONSE_DESTINATION = "/queue/patient-transfers.responses";
    private static final String TRANSFER_COMPLETE_DESTINATION = "/queue/patient-transfers.complete";

    private final SendTransferRequestService sendTransferRequestService;
    private final SendTransferCompleteService sendTransferCompleteService;

    private final StompEventPublisher stompEventPublisher;

    @MessageMapping("/patient-transfers/requests")
    public void sendTransferRequest(@Payload PatientTransferRequestPayload payload, Principal principal) {
        log.info("/patient-transfers/requests payload: {}", payload);
        Long ambulanceId = Long.valueOf(principal.getName());
        sendTransferRequestService.sendMessage(payload, ambulanceId);
    }

    @MessageMapping("/patient-transfers/responses")
    public void sendTransferResponse(@Payload PatientTransferResponsePayload payload, Principal principal) {
        log.info("/patient-transfers/responses payload: {}", payload);
        Long hospitalId = Long.valueOf(principal.getName());

        PatientTransferResponseMessage message = PatientTransferResponseMessage.builder()
                .hospitalId(hospitalId)
                .accepted(payload.accepted())
                .build();

        stompEventPublisher.sendToUser(
                payload.ambulanceId().toString(),
                TRANSFER_RESPONSE_DESTINATION,
                EventType.TRANSFER_RESPONSE,
                message);
    }

    @MessageMapping("/patient-transfers/complete")
    public void completeTransfer(@Payload PatientTransferCompletePayload payload, Principal principal) {
        log.info("/patient-transfers/complete payload: {}", payload);
        Long ambulanceId = Long.valueOf(principal.getName());

        CreateTransferCompleteMessageCommand command = CreateTransferCompleteMessageCommand.builder()
                .ambulanceId(ambulanceId)
                .acceptedHospitalId(payload.hospitalId())
                .build();

        sendTransferCompleteService.createMessage(command).messagePlans()
                .forEach(messagePlan -> {
                    PatientTransferCompleteMessage message = switch (messagePlan.type()) {
                        case ACCEPT -> PatientTransferCompleteMessage.AcceptMessage(ambulanceId);
                        case DENY -> PatientTransferCompleteMessage.DenyMessage(ambulanceId);
                    };
                    stompEventPublisher.sendToUser(
                            messagePlan.hospitalId().toString(),
                            TRANSFER_COMPLETE_DESTINATION,
                            EventType.TRANSFER_COMPLETE,
                            message);
                });
    }
}