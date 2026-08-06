package com.aiems.be.modules.ambulance.web.controller;

import com.aiems.be.common.util.SecurityUtil;
import com.aiems.be.modules.ambulance.service.TransferJournalService;
import com.aiems.be.modules.ambulance.web.request.TransferJournalRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai")
public class TransferJournalController {

    private final TransferJournalService transferJournalService;

    @PostMapping("/summary")
    public ResponseEntity<Void> requestJournal(@Valid @RequestBody TransferJournalRequest request) {
        Long ambulanceId = Long.valueOf(SecurityUtil.getCurrentUserId());
        transferJournalService.requestJournal(ambulanceId, request.message());
        return ResponseEntity.accepted().build();
    }
}
