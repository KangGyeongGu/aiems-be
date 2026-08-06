package com.aiems.be.modules.transfer.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.aiems.be.modules.transfer.service.result.CreateTransferDoneMessageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true")
@Service
public class SendTransferDoneService {

    private final TransferRecordService transferRecordService;

    @Transactional(readOnly = true)
    public CreateTransferDoneMessageResult createTransferDoneMessage(Long ambulanceId) {
        Long visitedHospitalId = transferRecordService.searchOngoingTransfer(ambulanceId).getHospitalId();

        return CreateTransferDoneMessageResult.builder()
                .visitedHospitalId(visitedHospitalId)
                .ambulanceId(ambulanceId)
                .isDone(true)
                .build();
    }
}