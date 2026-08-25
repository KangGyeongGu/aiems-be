package com.aiems.be.hospital.web.controller;

import com.aiems.be.common.util.SecurityUtil;
import com.aiems.be.contracts.transfer.TransferResponseMessage;
import com.aiems.be.contracts.websocket.EventType;
import com.aiems.be.hospital.messaging.NotificationPublisher;
import com.aiems.be.hospital.web.request.TransferResponseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hospital/transfers")
public class TransferResponseController {

    private static final String TRANSFER_RESPONSE_DESTINATION = "/queue/patient-transfers.responses";

    private final NotificationPublisher notificationPublisher;

    @PostMapping("/responses")
    public ResponseEntity<Void> respond(@RequestBody TransferResponseRequest request) {
        Long hospitalId = Long.valueOf(SecurityUtil.getCurrentUserId());

        notificationPublisher.sendToUser(
                request.ambulanceId().toString(),
                TRANSFER_RESPONSE_DESTINATION,
                EventType.TRANSFER_RESPONSE,
                TransferResponseMessage.of(hospitalId, request.accepted()));

        return ResponseEntity.ok().build();
    }
}
