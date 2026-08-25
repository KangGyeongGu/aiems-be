package com.aiems.be.transfer.service;

import com.aiems.be.contracts.transfer.TransferCompleteMessage;
import com.aiems.be.transfer.messaging.NotificationPublisher;
import com.aiems.be.contracts.websocket.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferConfirmService {

    private static final String TRANSFER_COMPLETE_DESTINATION = "/queue/patient-transfers.complete";

    private final TransferSnapshotService transferSnapshotService;
    private final TransferRecordService transferRecordService;
    private final PatientService patientService;
    private final NotificationPublisher notificationPublisher;

    @Transactional
    public void confirm(Long ambulanceId, Long acceptedHospitalId, String hospitalName, String hospitalAddress) {
        Long patientId = patientService.findCurrentPatient(ambulanceId).getId();
        transferRecordService.open(ambulanceId, acceptedHospitalId, hospitalName, hospitalAddress, patientId, Instant.now());

        transferSnapshotService.find(ambulanceId).forEach(hospitalId -> {
            TransferCompleteMessage message = hospitalId.equals(acceptedHospitalId)
                    ? TransferCompleteMessage.accept(ambulanceId)
                    : TransferCompleteMessage.deny(ambulanceId);

            notificationPublisher.sendToUser(
                    hospitalId.toString(),
                    TRANSFER_COMPLETE_DESTINATION,
                    EventType.TRANSFER_COMPLETE,
                    message);
        });
        log.info("이송 확정: ambulanceId={}, hospitalId={}", ambulanceId, acceptedHospitalId);
    }
}
