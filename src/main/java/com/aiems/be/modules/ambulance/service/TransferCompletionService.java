package com.aiems.be.modules.ambulance.service;

import com.aiems.be.common.exception.BusinessException;
import com.aiems.be.modules.ambulance.exception.AmbulanceErrorCode;
import com.aiems.be.modules.ambulance.repository.AmbulanceRepository;
import com.aiems.be.modules.auth.domain.Ambulance;
import com.aiems.be.modules.auth.domain.OperationStatus;
import com.aiems.be.modules.transfer.domain.TransferRecord;
import com.aiems.be.modules.transfer.event.TransferDoneMessage;
import com.aiems.be.modules.transfer.service.TransferRecordService;
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
public class TransferCompletionService {

    private static final String TRANSFER_DONE_DESTINATION = "/queue/patient-transfers.done";

    private final TransferRecordService transferRecordService;
    private final AmbulanceRepository ambulanceRepository;
    private final StompEventPublisher stompEventPublisher;

    @Transactional
    public void complete(Long ambulanceId, Instant completedAt) {
        TransferRecord closed = transferRecordService.close(ambulanceId, completedAt);

        Ambulance ambulance = ambulanceRepository.findById(ambulanceId)
                .orElseThrow(() -> new BusinessException(AmbulanceErrorCode.AMBULANCE_NOT_FOUND));
        ambulance.updateNextStatus(OperationStatus.STANDBY);

        stompEventPublisher.sendToUser(
                closed.getHospitalId().toString(),
                TRANSFER_DONE_DESTINATION,
                EventType.TRANSFER_DONE,
                TransferDoneMessage.done(ambulanceId));
        log.info("이송 완료: ambulanceId={}, hospitalId={}", ambulanceId, closed.getHospitalId());
    }
}
