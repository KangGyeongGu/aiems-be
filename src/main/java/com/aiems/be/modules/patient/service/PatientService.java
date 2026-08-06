package com.aiems.be.modules.patient.service;

import com.aiems.be.modules.patient.domain.Patient;
import com.aiems.be.modules.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional
    public Patient savePatient(Patient patient) {
        patientRepository.save(patient);
        return patient;
    }
}
