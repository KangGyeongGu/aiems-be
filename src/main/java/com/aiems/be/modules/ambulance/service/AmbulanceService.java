package com.aiems.be.modules.ambulance.service;

import com.aiems.be.modules.ambulance.domain.Ambulance;
import com.aiems.be.modules.ambulance.domain.OperationStatus;
import com.aiems.be.modules.ambulance.repository.AmbulanceRepository;
import com.aiems.be.modules.ambulance.service.command.CompleteCurrentTransferCommand;
import com.aiems.be.modules.patient.domain.Patient;
import com.aiems.be.modules.patient.repository.PatientRepository;
import com.aiems.be.modules.transfer.domain.TransferRecord;
import com.aiems.be.modules.transfer.repository.TransferRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AmbulanceService {

    private final AmbulanceRepository ambulanceRepository;
    private final PatientRepository patientRepository;
    private final TransferRecordRepository transferRecordRepository;

    public Ambulance searchById(Long ambulanceId) {
        return ambulanceRepository.findById(ambulanceId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 구급차 입니다. [id: %s]".formatted(ambulanceId)));
    }

    public Patient searchMyPatient(Long ambulanceId) {
        return patientRepository.searchMyCurrentPatient(ambulanceId);
    }

    @Transactional
    public void changeOperationStatus(Long ambulanceId, OperationStatus nextStatus) {
        Ambulance ambulance = searchById(ambulanceId);
        ambulance.updateNextStatus(nextStatus);
    }

    @Transactional
    public void completeCurrentTransfer(CompleteCurrentTransferCommand command) {
        changeOperationStatus(command.ambulanceId(), OperationStatus.STANDBY);

        Patient patient = patientRepository.searchMyCurrentPatient(command.ambulanceId());
        TransferRecord transferRecord = transferRecordRepository.findByPatientId(patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("해당 환자의 이송 기록이 존재하지 않습니다. [patientId = %s]".formatted(patient.getId())));
        transferRecord.markCompleteTime(command.completedAt());
    }
}
