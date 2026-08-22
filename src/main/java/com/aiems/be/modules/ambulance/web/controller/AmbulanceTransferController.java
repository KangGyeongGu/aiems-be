package com.aiems.be.modules.ambulance.web.controller;

import com.aiems.be.common.storage.MinioStorage;
import com.aiems.be.common.util.SecurityUtil;
import com.aiems.be.modules.ambulance.service.TransferCompletionService;
import com.aiems.be.modules.ambulance.service.TransferConfirmService;
import com.aiems.be.modules.ambulance.service.TransferRequestService;
import com.aiems.be.modules.ambulance.web.request.TransferConfirmRequest;
import com.aiems.be.modules.ambulance.web.request.TransferRequest;
import com.aiems.be.modules.ambulance.web.response.AmbulanceTransferSummaryResponse;
import com.aiems.be.modules.ambulance.web.response.TransferReportResponse;
import com.aiems.be.modules.transfer.service.TransferRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@RequestMapping("/api/v1/ambulance/transfers")
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true")
public class AmbulanceTransferController {

    private final TransferRequestService transferRequestService;
    private final TransferConfirmService transferConfirmService;
    private final TransferCompletionService transferCompletionService;
    private final TransferRecordService transferRecordService;
    private final MinioStorage minioStorage;

    @PostMapping
    public ResponseEntity<Void> request(@RequestBody TransferRequest request) {
        Long ambulanceId = Long.valueOf(SecurityUtil.getCurrentUserId());
        transferRequestService.request(request, ambulanceId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/confirmation")
    public ResponseEntity<Void> confirm(@RequestBody TransferConfirmRequest request) {
        Long ambulanceId = Long.valueOf(SecurityUtil.getCurrentUserId());
        transferConfirmService.confirm(ambulanceId, request.hospitalId());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/finish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> finish(@RequestPart("audio") MultipartFile audio) throws IOException {
        Long ambulanceId = Long.valueOf(SecurityUtil.getCurrentUserId());
        String audioKey = minioStorage.uploadVoice(
                ambulanceId, audio.getBytes(), audio.getContentType(), audio.getOriginalFilename());
        transferCompletionService.finish(ambulanceId, audioKey, Instant.now());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<AmbulanceTransferSummaryResponse>> list(
            @PageableDefault(size = 20, sort = "startedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long ambulanceId = Long.valueOf(SecurityUtil.getCurrentUserId());
        Page<AmbulanceTransferSummaryResponse> records = transferRecordService
                .getMySummaries(ambulanceId, pageable)
                .map(AmbulanceTransferSummaryResponse::from);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{transferRecordId}/report")
    public ResponseEntity<TransferReportResponse> report(@PathVariable Long transferRecordId) {
        Long ambulanceId = Long.valueOf(SecurityUtil.getCurrentUserId());
        String reportKey = transferRecordService.getReportKey(transferRecordId, ambulanceId);
        if (reportKey == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(new TransferReportResponse(minioStorage.presignReport(reportKey)));
    }
}
