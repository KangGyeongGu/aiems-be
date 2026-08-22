package com.aiems.be.transfer.web.controller;

import com.aiems.be.common.util.SecurityUtil;
import com.aiems.be.transfer.service.TransferCompletionService;
import com.aiems.be.transfer.service.TransferConfirmService;
import com.aiems.be.transfer.service.TransferRecordService;
import com.aiems.be.transfer.service.TransferRequestService;
import com.aiems.be.transfer.storage.MinioStorage;
import com.aiems.be.transfer.web.request.TransferConfirmRequest;
import com.aiems.be.transfer.web.request.TransferRequest;
import com.aiems.be.transfer.web.response.AmbulanceTransferSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferRequestService transferRequestService;
    private final TransferConfirmService transferConfirmService;
    private final TransferCompletionService transferCompletionService;
    private final TransferRecordService transferRecordService;
    private final MinioStorage minioStorage;

    @PostMapping
    public ResponseEntity<Void> request(@RequestBody TransferRequest request) {
        transferRequestService.request(request, currentAmbulanceId());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/confirmation")
    public ResponseEntity<Void> confirm(@RequestBody TransferConfirmRequest request) {
        transferConfirmService.confirm(
                currentAmbulanceId(), request.hospitalId(), request.hospitalName(), request.hospitalAddress());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/finish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> finish(@RequestPart("audio") MultipartFile audio) throws IOException {
        Long ambulanceId = currentAmbulanceId();
        String audioKey = minioStorage.uploadVoice(
                ambulanceId, audio.getBytes(), audio.getContentType(), audio.getOriginalFilename());
        transferCompletionService.finish(ambulanceId, audioKey, Instant.now());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<AmbulanceTransferSummaryResponse>> list(
            @PageableDefault(size = 20, sort = "startedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AmbulanceTransferSummaryResponse> records = transferRecordService
                .getMySummaries(currentAmbulanceId(), pageable)
                .map(AmbulanceTransferSummaryResponse::from);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{transferRecordId}/report")
    public ResponseEntity<TransferReportResponse> report(@PathVariable Long transferRecordId) {
        String reportKey = transferRecordService.getReportKey(transferRecordId, currentAmbulanceId());
        if (reportKey == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(new TransferReportResponse(minioStorage.presignReport(reportKey)));
    }

    private Long currentAmbulanceId() {
        return Long.valueOf(SecurityUtil.getCurrentUserId());
    }

    public record TransferReportResponse(String url) {
    }
}
