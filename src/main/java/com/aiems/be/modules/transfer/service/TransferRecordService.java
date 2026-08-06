package com.aiems.be.modules.transfer.service;

import com.aiems.be.common.exception.BusinessException;
import com.aiems.be.common.exception.CommonErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.aiems.be.modules.ambulance.service.AmbulanceService;
import com.aiems.be.modules.patient.domain.Patient;
import com.aiems.be.modules.transfer.domain.TransferRecord;
import com.aiems.be.modules.transfer.repository.TransferRecordRepository;
import com.aiems.be.modules.transfer.repository.dto.TransferRecordDto;
import com.aiems.be.modules.transfer.repository.dto.TransferRecordSummaryDto;
import com.aiems.be.modules.transfer.service.command.TransferRecordInitCommand;
import com.aiems.be.modules.transfer.web.request.TransferRecordSearchRequest;
import com.aiems.be.modules.transfer.web.response.TransferRecordResponse;
import com.aiems.be.modules.transfer.web.response.TransferRecordSummaryResponse;
import com.aiems.be.modules.transfer.web.response.TreatmentRecordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class TransferRecordService {

    private final TransferRecordRepository transferRecordRepository;
    private final AmbulanceService ambulanceService;

    @Transactional(readOnly = true)
    public TreatmentRecordResponse searchTransferRecord(Long transferRecordId) {
        TransferRecordDto transferRecord = transferRecordRepository.findByTransferRecordId(transferRecordId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        TreatmentRecordResponse response = null;
        try {
            response = mapper.readValue(transferRecord.getTreatmentRecord(), TreatmentRecordResponse.class);
        } catch (Exception e) {
            log.warn("이송 기록의 치료 기록을 파싱하는데 실패했습니다. transferRecordId: {}", transferRecordId, e);
            throw new RuntimeException(e);
        }

        return response;
    }

    @Transactional
    public void saveTreatmentRecord(Long ambulanceId, String treatmentRecord) {
        Long patientId = ambulanceService.searchMyPatient(ambulanceId).getId();
        transferRecordRepository.findByAmbulanceIdAndPatientId(ambulanceId, patientId)
                .ifPresent((transferRecord) -> {
                    transferRecord.updateTreatmentRecord(treatmentRecord);
                });
    }

    @Transactional
    public TransferRecord initRecord(TransferRecordInitCommand command) {
        Patient patient = ambulanceService.searchMyPatient(command.ambulanceId());

        TransferRecord record = TransferRecord.builder()
                .ambulanceId(command.ambulanceId())
                .patientId(patient.getId())
                .hospitalId(command.hospitalId())
                .startedAt(command.startedAt())
                .build();

        return transferRecordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public TransferRecordResponse getTransferRecord(Long transferRecordId) {
        TransferRecordDto transferRecord = transferRecordRepository.findByTransferRecordId(transferRecordId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND, "조회된 기록이 없습니다."));

        return TransferRecordResponse.from(transferRecord);
    }

    @Transactional(readOnly = true)
    public Page<TransferRecordSummaryResponse> getTransferRecords(
            Long hospitalId,
            Pageable pageable,
            TransferRecordSearchRequest searchRequest) {
        Page<TransferRecordSummaryDto> transferRecords = transferRecordRepository.findAllSummaries(
                hospitalId,
                searchRequest.getPatientName(),
                searchRequest.getPatientAge(),
                searchRequest.getPatientGender(),
                searchRequest.getSymptoms(),
                searchRequest.getPreKTAS(),
                searchRequest.getAmbulanceLicensePlate(),
                searchRequest.getFireStationName(),
                searchRequest.getStartedAtFrom(),
                searchRequest.getStartedAtTo(),
                searchRequest.getEndedAtFrom(),
                searchRequest.getEndedAtTo(),
                pageable
        );
        return transferRecords.map(TransferRecordSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public TransferRecord searchOngoingTransfer(Long ambulanceId) {
        return transferRecordRepository.findOngoingTransferRecord(ambulanceId)
                .orElseThrow(() -> new IllegalStateException("해당 구급차의 진행 중인 이송 기록이 없습니다. [ambulanceId=%s]".formatted(ambulanceId)));
    }

}