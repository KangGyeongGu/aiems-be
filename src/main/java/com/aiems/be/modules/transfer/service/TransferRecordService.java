package com.aiems.be.modules.transfer.service;

import com.aiems.be.common.exception.BusinessException;
import com.aiems.be.modules.transfer.domain.Gender;
import com.aiems.be.modules.transfer.domain.PreKTAS;
import com.aiems.be.modules.transfer.domain.TransferRecord;
import com.aiems.be.modules.transfer.exception.TransferErrorCode;
import com.aiems.be.modules.transfer.repository.TransferRecordRepository;
import com.aiems.be.modules.transfer.repository.projection.TransferRecordDetail;
import com.aiems.be.modules.transfer.repository.projection.TransferRecordSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TransferRecordService {

    private final TransferRecordRepository transferRecordRepository;
    private final PatientService patientService;

    @Transactional
    public TransferRecord open(Long ambulanceId, Long hospitalId, Long patientId, Instant startedAt) {
        TransferRecord record = TransferRecord.builder()
                .ambulanceId(ambulanceId)
                .hospitalId(hospitalId)
                .patientId(patientId)
                .startedAt(startedAt)
                .build();

        return transferRecordRepository.save(record);
    }

    @Transactional
    public TransferRecord close(Long ambulanceId, Instant completedAt) {
        TransferRecord ongoing = transferRecordRepository.findOngoingTransferRecord(ambulanceId)
                .orElseThrow(() -> new BusinessException(TransferErrorCode.ONGOING_TRANSFER_NOT_FOUND));

        ongoing.markCompleteTime(completedAt);
        return ongoing;
    }

    @Transactional
    public void saveJournal(Long ambulanceId, String journalJson) {
        Long patientId = patientService.findCurrentPatient(ambulanceId).getId();
        transferRecordRepository.findByAmbulanceIdAndPatientId(ambulanceId, patientId)
                .ifPresent(record -> record.updateTreatmentRecord(journalJson));
    }

    @Transactional(readOnly = true)
    public TransferRecordDetail getDetail(Long transferRecordId) {
        return transferRecordRepository.findByTransferRecordId(transferRecordId)
                .orElseThrow(() -> new BusinessException(TransferErrorCode.TRANSFER_RECORD_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public String getJournalJson(Long transferRecordId) {
        return getDetail(transferRecordId).treatmentRecord();
    }

    @Transactional(readOnly = true)
    public Page<TransferRecordSummary> getSummaries(Long hospitalId, TransferRecordSearch search, Pageable pageable) {
        return transferRecordRepository.findAllSummaries(
                hospitalId,
                search.patientName(),
                search.patientAge(),
                search.patientGender(),
                search.symptoms(),
                search.preKtas(),
                search.ambulanceLicensePlate(),
                search.fireStationName(),
                search.startedAtFrom(),
                search.startedAtTo(),
                search.endedAtFrom(),
                search.endedAtTo(),
                pageable
        );
    }

    public record TransferRecordSearch(
            String patientName,
            Integer patientAge,
            Gender patientGender,
            String symptoms,
            PreKTAS preKtas,
            String ambulanceLicensePlate,
            String fireStationName,
            Instant startedAtFrom,
            Instant startedAtTo,
            Instant endedAtFrom,
            Instant endedAtTo
    ) {
    }
}
