package com.aiems.be.modules.transfer.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.aiems.be.modules.ambulance.domain.Ambulance;
import com.aiems.be.modules.ambulance.service.AmbulanceService;
import com.aiems.be.modules.hospital.service.HospitalService;
import com.aiems.be.modules.hospital.service.command.HospitalRecommendCommand;
import com.aiems.be.modules.hospital.service.result.HospitalRecommendResult;
import com.aiems.be.modules.hospital.service.result.ScoredHospital;
import com.aiems.be.modules.patient.domain.Patient;
import com.aiems.be.modules.patient.service.PatientAnalysisService;
import com.aiems.be.modules.patient.service.PatientService;
import com.aiems.be.modules.patient.service.request.PatientAnalysisRequest;
import com.aiems.be.modules.patient.service.result.PatientAnalysisResult;
import com.aiems.be.modules.transfer.service.command.TransferRequestSnapshotCommand;
import com.aiems.be.modules.transfer.web.message.PatientTransferRequestMessage;
import com.aiems.be.modules.transfer.web.message.RequestedHospitalListMessage;
import com.aiems.be.modules.transfer.web.message.RequestedHospitalMessage;
import com.aiems.be.modules.transfer.web.payload.PatientTransferRequestPayload;
import lombok.RequiredArgsConstructor;
import com.aiems.be.websocket.event.EventType;
import com.aiems.be.websocket.event.StompEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true")
@Service
public class SendTransferRequestService {

    private static final int MAX_REQUESTED_HOSPITAL_COUNT = 10;

    private static final String TRANSFER_REQUEST_DESTINATION = "/queue/patient-transfers.requests";
    private static final String REQUESTED_HOSPITAL_DESTINATION = "/queue/patient-transfers.requested-hospitals";

    private final StompEventPublisher stompEventPublisher;
    private final HospitalService hospitalService;
    private final PatientAnalysisService patientAnalysisService;
    private final AmbulanceService ambulanceService;
    private final PatientService patientService;
    private final TransferSnapshotService transferSnapshotService;

    @Transactional
    public void sendMessage(PatientTransferRequestPayload payload, Long ambulanceId) {
        PatientAnalysisResult analysisResult = analyzePatient(payload);

        Ambulance ambulance = ambulanceService.searchById(ambulanceId);
        Patient patient = patientService.savePatient(payload.toEntity(analysisResult.preKTAS(), ambulance));

        HospitalRecommendResult recommendResult = recommendHospital(payload, analysisResult);

        List<ScoredHospital> requestedHospitals = sendTransferRequestNotification(payload, recommendResult, patient, ambulance);
        saveTransferRequestSnapshot(ambulanceId, requestedHospitals);
        sendRequestedHospitalList(ambulanceId, requestedHospitals);
    }

    private PatientAnalysisResult analyzePatient(PatientTransferRequestPayload payload) {
        PatientAnalysisRequest request = payload.toAnalysisRequest();
        PatientAnalysisResult analysisResult = patientAnalysisService.analyze(request);
        log.info("AnalysisResult: {}", analysisResult);

        return analysisResult;
    }

    private HospitalRecommendResult recommendHospital(PatientTransferRequestPayload payload, PatientAnalysisResult analysisResult) {
        HospitalRecommendCommand recommendCommand = HospitalRecommendCommand.from(payload.accidentLocation(), analysisResult);
        HospitalRecommendResult recommendResult = hospitalService.recommendHospital(recommendCommand);

        recommendResult.hospitals()
                .forEach(scoredHospital -> {
                    log.info("추천 병원: {}, 점수: {}", scoredHospital.hospital().getName(), scoredHospital.score());
                });
        return recommendResult;
    }

    private List<ScoredHospital> sendTransferRequestNotification(
            PatientTransferRequestPayload payload,
            HospitalRecommendResult recommendResult,
            Patient patient,
            Ambulance ambulance
    ) {
        List<ScoredHospital> requestedHospitals = new ArrayList<>();

        //추천된 병원들에게 이송 요청 메시지 전송
        recommendResult.hospitals().stream()
                .sorted()
                .limit(MAX_REQUESTED_HOSPITAL_COUNT)
                .forEach(scoredHospital -> {
                    sendTransferRequestMessage(payload, patient, ambulance, scoredHospital);
                    requestedHospitals.add(scoredHospital);
                });

        return requestedHospitals;
    }

    private void sendTransferRequestMessage(
            PatientTransferRequestPayload payload,
            Patient patient,
            Ambulance ambulance,
            ScoredHospital requestHospital
    ) {
        PatientTransferRequestMessage message = PatientTransferRequestMessage.of(patient, ambulance, payload, requestHospital.distance());

        Long requestHospitalId = requestHospital.hospital().getId();
        stompEventPublisher.sendToUser(
                requestHospitalId.toString(),
                TRANSFER_REQUEST_DESTINATION,
                EventType.TRANSFER_REQUEST,
                message);
        log.info("요청된 병원: {}", requestHospital);
    }

    private void saveTransferRequestSnapshot(Long ambulanceId, List<ScoredHospital> requestedHospitals) {
        List<Long> requestedHospitalIds = requestedHospitals.stream()
                .map(scoredHospital -> scoredHospital.hospital().getId())
                .toList();

        TransferRequestSnapshotCommand command = TransferRequestSnapshotCommand.builder()
                .ambulanceId(ambulanceId)
                .hospitalIds(requestedHospitalIds)
                .build();
        transferSnapshotService.saveTransferRequestSnapshot(command);
    }

    private void sendRequestedHospitalList(Long ambulanceId, List<ScoredHospital> requestedHospitals) {
        List<RequestedHospitalMessage> hospitalMessageList = requestedHospitals.stream()
                .map(RequestedHospitalMessage::from)
                .toList();

        RequestedHospitalListMessage message = RequestedHospitalListMessage.builder()
                .hospitals(hospitalMessageList)
                .build();

        stompEventPublisher.sendToUser(ambulanceId.toString(), REQUESTED_HOSPITAL_DESTINATION, EventType.TRANSFER_REQUESTED_HOSPITALS, message);
        log.info("AmbulanceId={}, 이송 요청 병원 목록: {}", ambulanceId, message);
    }

}