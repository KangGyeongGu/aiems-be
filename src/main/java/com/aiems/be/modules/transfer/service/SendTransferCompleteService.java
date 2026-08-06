package com.aiems.be.modules.transfer.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.aiems.be.modules.transfer.service.command.CreateTransferCompleteMessageCommand;
import com.aiems.be.modules.transfer.service.command.TransferRecordInitCommand;
import com.aiems.be.modules.transfer.service.result.CreateTransferCompleteMessageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static com.aiems.be.modules.transfer.service.result.CreateTransferCompleteMessageResult.MessagePlan;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true")
@Service
public class SendTransferCompleteService {

    private final TransferRecordService transferRecordService;
    private final TransferSnapshotService transferSnapshotService;

    @Transactional
    public CreateTransferCompleteMessageResult createMessage(CreateTransferCompleteMessageCommand command) {
        //Snapshot에서 이송 요청된 병원 목록을 바탕으로 거절 메시지 계획 생성
        Long ambulanceId = command.ambulanceId();
        List<MessagePlan> messagePlans = transferSnapshotService.getTransferRequestSnapshot(ambulanceId).stream()
                .map(hospitalId -> {
                    if (command.isDeniedHospital(hospitalId)) {
                        return MessagePlan.deny(hospitalId);
                    } else {
                        return MessagePlan.accept(hospitalId);
                    }
                })
                .toList();

        Long acceptedHospitalId = command.acceptedHospitalId();
        recordTransfer(acceptedHospitalId, ambulanceId);

        return CreateTransferCompleteMessageResult.builder()
                .ambulanceId(ambulanceId)
                .messagePlans(messagePlans)
                .build();
    }

    private void recordTransfer(Long acceptedHospitalId, Long ambulanceId) {
        transferRecordService.initRecord(TransferRecordInitCommand.builder()
                .hospitalId(acceptedHospitalId)
                .ambulanceId(ambulanceId)
                .startedAt(Instant.now())
                .build());
    }

}