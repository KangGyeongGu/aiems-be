package com.aiems.be.modules.ambulance.service;

import com.aiems.be.modules.transfer.domain.Patient;
import com.aiems.be.modules.transfer.messaging.client.AiMessageClient;
import com.aiems.be.modules.transfer.messaging.contract.SummaryJobMessage;
import com.aiems.be.modules.transfer.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferJournalService {

    private final PatientService patientService;
    private final AiMessageClient aiMessageClient;

    @Transactional(readOnly = true)
    public void requestJournal(Long ambulanceId, String message) {
        Patient patient = patientService.findCurrentPatient(ambulanceId);
        aiMessageClient.publishSummaryRequest(SummaryJobMessage.request(ambulanceId, message, patient));
    }
}
