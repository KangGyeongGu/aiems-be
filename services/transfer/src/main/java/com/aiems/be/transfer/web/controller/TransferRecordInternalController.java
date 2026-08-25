package com.aiems.be.transfer.web.controller;

import com.aiems.be.contracts.transfer.record.TransferRecordResponse;
import com.aiems.be.contracts.transfer.record.TransferRecordSearchRequest;
import com.aiems.be.contracts.transfer.record.TransferRecordSummaryResponse;
import com.aiems.be.contracts.transfer.record.TreatmentRecordResponse;
import com.aiems.be.transfer.domain.Patient;
import com.aiems.be.transfer.repository.projection.TransferRecordDetail;
import com.aiems.be.transfer.repository.projection.TransferRecordSummary;
import com.aiems.be.transfer.service.TransferRecordService;
import com.aiems.be.transfer.service.TransferRecordService.TransferRecordSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;
import com.aiems.be.contracts.common.PageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/transfers/records")
public class TransferRecordInternalController {

    private final TransferRecordService transferRecordService;

    @GetMapping("/{transferRecordId}")
    public TransferRecordResponse getTransferRecord(@PathVariable Long transferRecordId) {
        return toResponse(transferRecordService.getDetail(transferRecordId));
    }

    @GetMapping
    public PageResponse<TransferRecordSummaryResponse> getTransferRecords(
            @RequestParam Long hospitalId,
            @PageableDefault Pageable pageable,
            TransferRecordSearchRequest searchRequest
    ) {
        Page<TransferRecordSummaryResponse> result = transferRecordService
                .getSummaries(hospitalId, toSearch(searchRequest), pageable)
                .map(TransferRecordInternalController::toSummaryResponse);
        return new PageResponse<>(result.getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    @GetMapping("/{transferRecordId}/treatments")
    public TreatmentRecordResponse getTreatmentRecord(@PathVariable Long transferRecordId) {
        return TreatmentRecordResponse.parse(transferRecordService.getJournalJson(transferRecordId));
    }

    private static TransferRecordResponse toResponse(TransferRecordDetail detail) {
        return new TransferRecordResponse(
                detail.id(),
                patientDetail(detail.patient()),
                new TransferRecordResponse.AmbulanceDetail(
                        detail.ambulanceId(), detail.ambulanceLicensePlate(), detail.ambulanceFireStationName()),
                new TransferRecordResponse.HospitalDetail(
                        detail.hospitalId(), detail.hospitalName(), detail.hospitalAddress()),
                locationDetail(detail.patient()),
                detail.startedAt(),
                detail.endedAt());
    }

    private static TransferRecordResponse.PatientDetail patientDetail(Patient patient) {
        return new TransferRecordResponse.PatientDetail(
                patient.getId(), patient.getName(), patient.getAge(),
                patient.getGender() != null ? patient.getGender().name() : null,
                patient.getSymptoms(),
                patient.getPreKtas() != null ? patient.getPreKtas().name() : null);
    }

    private static TransferRecordResponse.LocationDetail locationDetail(Patient patient) {
        if (patient.getLocation() == null) {
            return null;
        }
        var loc = patient.getLocation();
        return new TransferRecordResponse.LocationDetail(
                loc.getCoordinates().getY(), loc.getCoordinates().getX(), loc.getAddress());
    }

    private static TransferRecordSummaryResponse toSummaryResponse(TransferRecordSummary summary) {
        return new TransferRecordSummaryResponse(
                summary.id(),
                summary.endedAt(),
                new TransferRecordSummaryResponse.AmbulanceSummary(
                        summary.ambulanceLicensePlate(), summary.ambulanceFireStationName()),
                new TransferRecordSummaryResponse.PatientSummary(
                        summary.patientId(), summary.patientName(), summary.patientAge(),
                        summary.patientGender(), summary.patientSymptoms(), summary.preKtas()));
    }

    private static TransferRecordSearch toSearch(TransferRecordSearchRequest request) {
        return new TransferRecordSearch(
                request.patientName(), request.patientAge(), request.patientGender(),
                request.symptoms(), request.preKtas(),
                request.ambulanceLicensePlate(), request.fireStationName(),
                request.startedAtFrom(), request.startedAtTo(),
                request.endedAtFrom(), request.endedAtTo());
    }
}
