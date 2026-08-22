package com.aiems.be.transfer.service;

import com.aiems.be.common.exception.BusinessException;
import com.aiems.be.transfer.domain.Patient;
import com.aiems.be.transfer.exception.TransferErrorCode;
import com.aiems.be.transfer.repository.PatientRepository;
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

    @Transactional(readOnly = true)
    public Patient findById(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new BusinessException(TransferErrorCode.PATIENT_NOT_FOUND));
    }
}
