package com.aiems.be.modules.transfer.service;

import com.aiems.be.common.exception.BusinessException;
import com.aiems.be.modules.transfer.domain.Patient;
import com.aiems.be.modules.transfer.exception.TransferErrorCode;
import com.aiems.be.modules.transfer.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional
    public Patient register(Patient patient) {
        return patientRepository.save(patient);
    }

    @Transactional(readOnly = true)
    public Patient findCurrentPatient(Long ambulanceId) {
        return patientRepository.findTop1ByAmbulanceIdOrderByCreatedAtDesc(ambulanceId)
                .orElseThrow(() -> new BusinessException(TransferErrorCode.PATIENT_NOT_FOUND));
    }
}
