package com.aiems.be.modules.hospital.web.controller;

import com.aiems.be.common.util.SecurityUtil;
import com.aiems.be.modules.hospital.web.request.TransferRecordSearchRequest;
import com.aiems.be.modules.hospital.web.response.TransferRecordResponse;
import com.aiems.be.modules.hospital.web.response.TransferRecordSummaryResponse;
import com.aiems.be.modules.hospital.web.response.TreatmentRecordResponse;
import com.aiems.be.modules.transfer.service.TransferRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hospital/transfers")
public class TransferRecordController {

    private final TransferRecordService transferRecordService;

    @GetMapping("/{transferRecordId}")
    public ResponseEntity<TransferRecordResponse> getTransferRecord(@PathVariable Long transferRecordId) {
        return ResponseEntity.ok(TransferRecordResponse.from(transferRecordService.getDetail(transferRecordId)));
    }

    @GetMapping
    public ResponseEntity<Page<TransferRecordSummaryResponse>> getTransferRecords(
            @PageableDefault Pageable pageable,
            TransferRecordSearchRequest searchRequest
    ) {
        Long hospitalId = Long.valueOf(SecurityUtil.getCurrentUserId());
        Page<TransferRecordSummaryResponse> records = transferRecordService
                .getSummaries(hospitalId, searchRequest.toSearch(), pageable)
                .map(TransferRecordSummaryResponse::from);

        if (records.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{transferRecordId}/treatments")
    public ResponseEntity<TreatmentRecordResponse> getTreatmentRecord(@PathVariable Long transferRecordId) {
        String journalJson = transferRecordService.getJournalJson(transferRecordId);
        return ResponseEntity.ok(TreatmentRecordResponse.parse(journalJson));
    }
}
