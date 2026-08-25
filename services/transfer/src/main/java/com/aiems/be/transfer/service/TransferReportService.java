package com.aiems.be.transfer.service;

import com.aiems.be.transfer.domain.Patient;
import com.aiems.be.transfer.messaging.TransferMessageMapper;
import com.aiems.be.transfer.messaging.client.AiMessageClient;
import com.aiems.be.contracts.ai.SummaryJobMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferReportService {

    private final PatientService patientService;
    private final AiMessageClient aiMessageClient;

    @Transactional(readOnly = true)
    public void requestReport(Long ambulanceId, String audioKey) {
        Patient patient = patientService.findCurrentPatient(ambulanceId);
        SummaryJobMessage jobMessage = TransferMessageMapper.toSummaryJobMessage(ambulanceId, audioKey, patient);
        aiMessageClient.publishSummaryRequest(jobMessage);
    }
}
