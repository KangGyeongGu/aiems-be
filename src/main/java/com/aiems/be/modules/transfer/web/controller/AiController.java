package com.aiems.be.modules.transfer.web.controller;

import com.aiems.be.common.util.SecurityUtil;
import com.aiems.be.modules.ambulance.domain.Ambulance;
import com.aiems.be.modules.ambulance.service.AmbulanceService;
import com.aiems.be.modules.patient.domain.Patient;
import com.aiems.be.modules.patient.domain.VitalSign;
import com.aiems.be.modules.patient.service.PatientAnalysisService;
import com.aiems.be.modules.patient.service.request.PatientAnalysisRequest;
import com.aiems.be.modules.patient.service.result.PatientAnalysisResult;
import com.aiems.be.modules.transfer.service.ProducerService;
import com.aiems.be.modules.transfer.web.dto.AmbulanceDto;
import com.aiems.be.modules.transfer.web.dto.PatientDto;
import com.aiems.be.modules.transfer.web.dto.VitalSignDto;
import com.aiems.be.modules.transfer.web.message.SummaryJobMessage;
import com.aiems.be.modules.transfer.web.request.SummaryRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai")
public class AiController {

    private final ProducerService producerService;
    private final PatientAnalysisService patientAnalysisService;
    private final AmbulanceService ambulanceService;

    @PostMapping("/summary")
    public ResponseEntity<Void> sendSummary(@Valid @RequestBody SummaryRequest summaryRequest) {
        Long ambulanceId = Long.valueOf(SecurityUtil.getCurrentUserId());

        Patient patient = ambulanceService.searchMyPatient(ambulanceId);

        SummaryJobMessage jobMessage = SummaryJobMessage.builder()
                .ambulanceId(ambulanceId)
                .message(summaryRequest.message())
                .patient(toPatientDto(patient))
                .build();

        producerService.sendSummaryJobAsync(jobMessage);

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/classification")
    public ResponseEntity<PatientAnalysisResult> sendClassification(@RequestBody PatientAnalysisRequest patientAnalysisRequest) {
        return ResponseEntity.ok(patientAnalysisService.analyze(patientAnalysisRequest));
    }

    private PatientDto toPatientDto(Patient patient) {
        VitalSign vs = patient.getVitalSign();
        Ambulance amb = patient.getAmbulance();

        return PatientDto.builder()
                .id(patient.getId())
                .name(patient.getName())
                .age(patient.getAge())
                .gender(patient.getGender() != null ? patient.getGender().name() : null)
                .symptoms(patient.getSymptoms())
                .preKtas(patient.getPreKtas() != null ? patient.getPreKtas().name() : null)
                .firstAid(patient.getFirstAid())
                .cause(patient.getCause())
                .underlyingDisease(patient.getUnderlyingDisease())
                .vitalSign(vs != null ? VitalSignDto.builder()
                        .minBloodPressure(vs.getMinBloodPressure())
                        .maxBloodPressure(vs.getMaxBloodPressure())
                        .pulse(vs.getPulse())
                        .respiratoryRate(vs.getRespiratoryRate())
                        .temperature(vs.getTemperature())
                        .build() : null)
                .ambulance(amb != null ? AmbulanceDto.builder()
                        .id(amb.getId())
                        .deviceId(amb.getDeviceId())
                        .licensePlate(amb.getLicensePlate())
                        .fireStationName(amb.getFireStationName())
                        .jurisdiction(amb.getJurisdiction().name())
                        .operationStatus(amb.getOperationStatus().name())
                        .build() : null)
                .build();
    }
}