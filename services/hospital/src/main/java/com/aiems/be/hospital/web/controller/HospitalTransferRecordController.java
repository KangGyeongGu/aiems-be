package com.aiems.be.hospital.web.controller;

import com.aiems.be.common.util.SecurityUtil;
import com.aiems.be.contracts.transfer.record.TransferRecordResponse;
import com.aiems.be.contracts.transfer.record.TransferRecordSearchRequest;
import com.aiems.be.contracts.transfer.record.TransferRecordSummaryResponse;
import com.aiems.be.contracts.transfer.record.TreatmentRecordResponse;
import com.aiems.be.hospital.client.TransferRecordClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import com.aiems.be.contracts.common.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hospital/transfers")
public class HospitalTransferRecordController {

    private final TransferRecordClient transferRecordClient;

    @GetMapping("/{transferRecordId}")
    public ResponseEntity<TransferRecordResponse> getTransferRecord(@PathVariable Long transferRecordId) {
        return ResponseEntity.ok(transferRecordClient.getDetail(transferRecordId));
    }

    @GetMapping
    public ResponseEntity<Page<TransferRecordSummaryResponse>> getTransferRecords(
            @PageableDefault Pageable pageable,
            TransferRecordSearchRequest searchRequest
    ) {
        Long hospitalId = Long.valueOf(SecurityUtil.getCurrentUserId());
        List<String> sort = pageable.getSort().stream()
                .map(o -> o.getProperty() + "," + o.getDirection().name().toLowerCase())
                .toList();
        PageResponse<TransferRecordSummaryResponse> paged = transferRecordClient.getSummaries(
                hospitalId, pageable.getPageNumber(), pageable.getPageSize(), sort, searchRequest);

        Page<TransferRecordSummaryResponse> records =
                new PageImpl<>(paged.content(), pageable, paged.totalElements());

        if (records.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{transferRecordId}/treatments")
    public ResponseEntity<TreatmentRecordResponse> getTreatmentRecord(@PathVariable Long transferRecordId) {
        return ResponseEntity.ok(transferRecordClient.getTreatment(transferRecordId));
    }
}
