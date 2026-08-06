package com.aiems.be.modules.transfer.web.controller;

import com.aiems.be.common.util.SecurityUtil;
import com.aiems.be.modules.ambulance.service.AmbulanceService;
import com.aiems.be.modules.ambulance.service.command.CompleteCurrentTransferCommand;
import com.aiems.be.modules.transfer.service.SendTransferDoneService;
import com.aiems.be.modules.transfer.service.TransferRecordService;
import com.aiems.be.modules.transfer.service.result.CreateTransferDoneMessageResult;
import com.aiems.be.modules.transfer.web.message.PatientTransferDoneMessage;
import com.aiems.be.modules.transfer.web.request.CompleteCurrentTransferRequest;
import com.aiems.be.modules.transfer.web.request.TransferRecordSearchRequest;
import com.aiems.be.modules.transfer.web.response.TransferRecordResponse;
import com.aiems.be.modules.transfer.web.response.TransferRecordSummaryResponse;
import com.aiems.be.modules.transfer.web.response.TreatmentRecordResponse;
import com.aiems.be.websocket.event.EventType;
import com.aiems.be.websocket.event.StompEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true")
@RestController
public class TransferController {

    private static final String TRANSFER_DONE_DESTINATION = "/queue/patient-transfers.done";

    private final TransferRecordService transferRecordService;
    private final AmbulanceService ambulanceService;
    private final SendTransferDoneService sendTransferDoneService;

    private final StompEventPublisher stompEventPublisher;

    @GetMapping("/transfers/{transferRecordId}")
    public ResponseEntity<TransferRecordResponse> getTransferRecord(@PathVariable Long transferRecordId) {
        return ResponseEntity.ok(transferRecordService.getTransferRecord(transferRecordId));
    }

    @GetMapping("/transfers")
    public ResponseEntity<Page<TransferRecordSummaryResponse>> getTransferRecordList(
            @PageableDefault Pageable pageable,
            TransferRecordSearchRequest searchRequest
    ) {
        Long hospitalId = Long.valueOf(SecurityUtil.getCurrentUserId());
        Page<TransferRecordSummaryResponse> records = transferRecordService.getTransferRecords(hospitalId, pageable, searchRequest);

        if (records.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(records);
    }

    @PostMapping("/transfers/records")
    public ResponseEntity<Void> saveTransfer(@RequestBody CompleteCurrentTransferRequest request) {
        Long ambulanceId = Long.valueOf(SecurityUtil.getCurrentUserId());

        sendTransferDoneMessage(ambulanceId);
        ambulanceService.completeCurrentTransfer(CompleteCurrentTransferCommand.builder()
                .ambulanceId(ambulanceId)
                .completedAt(request.completedAt())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private void sendTransferDoneMessage(Long ambulanceId) {
        CreateTransferDoneMessageResult result = sendTransferDoneService.createTransferDoneMessage(ambulanceId);

        stompEventPublisher.sendToUser(
                result.visitedHospitalId().toString(),
                TRANSFER_DONE_DESTINATION,
                EventType.TRANSFER_DONE,
                PatientTransferDoneMessage.done(ambulanceId));
        log.info("환자 이송 완료 알림 전송 - visitedHospitalId: {}, ambulanceId: {}", result.visitedHospitalId(), ambulanceId);
    }

    @GetMapping("/transfers/{transferRecordId}/treatments")
    public ResponseEntity<TreatmentRecordResponse> getTransferTreatmentRecords(@PathVariable Long transferRecordId) {
        TreatmentRecordResponse response = transferRecordService.searchTransferRecord(transferRecordId);
        return ResponseEntity.ok(response);
    }

}