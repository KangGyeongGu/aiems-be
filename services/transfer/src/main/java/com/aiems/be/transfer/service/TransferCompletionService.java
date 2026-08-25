package com.aiems.be.transfer.service;

import com.aiems.be.transfer.domain.TransferRecord;
import com.aiems.be.contracts.transfer.TransferDoneMessage;
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
public class TransferCompletionService {

    private static final String TRANSFER_DONE_DESTINATION = "/queue/patient-transfers.done";

    private final TransferRecordService transferRecordService;
    private final NotificationPublisher notificationPublisher;
    private final TransferReportService transferReportService;

    @Transactional
    public void finish(Long ambulanceId, String audioKey, Instant completedAt) {
        TransferRecord closed = transferRecordService.close(ambulanceId, completedAt);

        notificationPublisher.sendToUser(
                closed.getHospitalId().toString(),
                TRANSFER_DONE_DESTINATION,
                EventType.TRANSFER_DONE,
                TransferDoneMessage.done(ambulanceId));
        log.info("이송 완료: ambulanceId={}, hospitalId={}", ambulanceId, closed.getHospitalId());

        transferReportService.requestReport(ambulanceId, audioKey);
    }
}
