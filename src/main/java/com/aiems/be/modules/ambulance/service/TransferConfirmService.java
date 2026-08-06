package com.aiems.be.modules.ambulance.service;

import com.aiems.be.modules.transfer.event.TransferCompleteMessage;
import com.aiems.be.modules.transfer.service.PatientService;
import com.aiems.be.modules.transfer.service.TransferRecordService;
import com.aiems.be.modules.transfer.service.TransferSnapshotService;
import com.aiems.be.websocket.event.EventType;
import com.aiems.be.websocket.event.StompEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true")
public class TransferConfirmService {

    private static final String TRANSFER_COMPLETE_DESTINATION = "/queue/patient-transfers.complete";

    private final TransferSnapshotService transferSnapshotService;
    private final TransferRecordService transferRecordService;
    private final PatientService patientService;
    private final StompEventPublisher stompEventPublisher;

    @Transactional
    public void confirm(Long ambulanceId, Long acceptedHospitalId) {
        Long patientId = patientService.findCurrentPatient(ambulanceId).getId();
        transferRecordService.open(ambulanceId, acceptedHospitalId, patientId, Instant.now());

        transferSnapshotService.find(ambulanceId).forEach(hospitalId -> {
            TransferCompleteMessage message = hospitalId.equals(acceptedHospitalId)
                    ? TransferCompleteMessage.accept(ambulanceId)
                    : TransferCompleteMessage.deny(ambulanceId);

            stompEventPublisher.sendToUser(
                    hospitalId.toString(),
                    TRANSFER_COMPLETE_DESTINATION,
                    EventType.TRANSFER_COMPLETE,
                    message);
        });
        log.info("이송 확정: ambulanceId={}, hospitalId={}", ambulanceId, acceptedHospitalId);
    }
}
