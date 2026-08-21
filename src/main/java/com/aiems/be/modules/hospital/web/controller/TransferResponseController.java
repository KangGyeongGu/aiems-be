package com.aiems.be.modules.hospital.web.controller;

import com.aiems.be.common.util.SecurityUtil;
import com.aiems.be.modules.hospital.web.request.TransferResponseRequest;
import com.aiems.be.modules.transfer.event.TransferResponseMessage;
import com.aiems.be.websocket.event.EventType;
import com.aiems.be.websocket.event.StompEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfers")
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true")
public class TransferResponseController {

    private static final String TRANSFER_RESPONSE_DESTINATION = "/queue/patient-transfers.responses";

    private final StompEventPublisher stompEventPublisher;

    @PostMapping("/responses")
    public ResponseEntity<Void> respond(@RequestBody TransferResponseRequest request) {
        Long hospitalId = Long.valueOf(SecurityUtil.getCurrentUserId());

        stompEventPublisher.sendToUser(
                request.ambulanceId().toString(),
                TRANSFER_RESPONSE_DESTINATION,
                EventType.TRANSFER_RESPONSE,
                TransferResponseMessage.of(hospitalId, request.accepted()));

        return ResponseEntity.ok().build();
    }
}
