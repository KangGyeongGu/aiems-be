package com.aiems.be.modules.ambulance.web.controller;

import com.aiems.be.common.util.SecurityUtil;
import com.aiems.be.modules.ambulance.service.TransferCompletionService;
import com.aiems.be.modules.ambulance.web.request.TransferCompletionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfers")
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true")
public class TransferCompletionController {

    private final TransferCompletionService transferCompletionService;

    @PostMapping("/records")
    public ResponseEntity<Void> complete(@RequestBody TransferCompletionRequest request) {
        Long ambulanceId = Long.valueOf(SecurityUtil.getCurrentUserId());
        transferCompletionService.complete(ambulanceId, request.completedAt());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
