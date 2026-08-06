package com.aiems.be.modules.ambulance.service;

import com.aiems.be.common.exception.BusinessException;
import com.aiems.be.modules.ambulance.exception.AmbulanceErrorCode;
import com.aiems.be.modules.ambulance.repository.AmbulanceRepository;
import com.aiems.be.modules.ambulance.socket.payload.TransferRequestPayload;
import com.aiems.be.modules.auth.domain.Ambulance;
import com.aiems.be.modules.hospital.service.HospitalRecommendService;
import com.aiems.be.modules.hospital.service.RecommendedHospital;
import com.aiems.be.modules.transfer.domain.Patient;
import com.aiems.be.modules.transfer.event.RecommendedHospitalsMessage;
import com.aiems.be.modules.transfer.event.TransferRequestMessage;
import com.aiems.be.modules.transfer.messaging.client.AiMessageClient;
import com.aiems.be.modules.transfer.messaging.contract.PatientAnalysisResult;
import com.aiems.be.modules.transfer.service.PatientService;
import com.aiems.be.modules.transfer.service.TransferSnapshotService;
import com.aiems.be.websocket.event.EventType;
import com.aiems.be.websocket.event.StompEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true")
public class TransferRequestService {

    private static final int MAX_REQUESTED_HOSPITAL_COUNT = 10;

    private static final String TRANSFER_REQUEST_DESTINATION = "/queue/patient-transfers.requests";
    private static final String REQUESTED_HOSPITAL_DESTINATION = "/queue/patient-transfers.requested-hospitals";

    private final AiMessageClient aiMessageClient;
    private final AmbulanceRepository ambulanceRepository;
    private final PatientService patientService;
    private final HospitalRecommendService hospitalRecommendService;
    private final TransferSnapshotService transferSnapshotService;
    private final StompEventPublisher stompEventPublisher;

    @Transactional
    public void request(TransferRequestPayload payload, Long ambulanceId) {
        PatientAnalysisResult analysis = aiMessageClient.requestClassification(payload.toAnalysisRequest());
        log.info("환자 분석 결과: {}", analysis);

        Ambulance ambulance = ambulanceRepository.findById(ambulanceId)
                .orElseThrow(() -> new BusinessException(AmbulanceErrorCode.AMBULANCE_NOT_FOUND));
        Patient patient = patientService.register(payload.toEntity(analysis.preKTAS(), ambulance));

        List<RecommendedHospital> candidates = hospitalRecommendService.recommend(
                        payload.accidentLocation().lon(),
                        payload.accidentLocation().lat(),
                        analysis.preKTAS(),
                        analysis.specialtyConfidences()).stream()
                .limit(MAX_REQUESTED_HOSPITAL_COUNT)
                .toList();

        notifyHospitals(candidates, patient, ambulance);
        transferSnapshotService.save(ambulanceId, candidates.stream()
                .map(candidate -> candidate.hospital().getId())
                .toList());
        replyToAmbulance(ambulanceId, candidates);
    }

    private void notifyHospitals(List<RecommendedHospital> candidates, Patient patient, Ambulance ambulance) {
        candidates.forEach(candidate -> {
            TransferRequestMessage message = TransferRequestMessage.of(patient, ambulance, candidate.distance());
            stompEventPublisher.sendToUser(
                    candidate.hospital().getId().toString(),
                    TRANSFER_REQUEST_DESTINATION,
                    EventType.TRANSFER_REQUEST,
                    message);
            log.info("이송 요청 전파: hospitalId={}, score={}", candidate.hospital().getId(), candidate.score());
        });
    }

    private void replyToAmbulance(Long ambulanceId, List<RecommendedHospital> candidates) {
        List<RecommendedHospitalsMessage.Item> items = candidates.stream()
                .map(candidate -> new RecommendedHospitalsMessage.Item(
                        candidate.hospital().getId(),
                        candidate.hospital().getName(),
                        candidate.hospital().getLocation().getAddress(),
                        candidate.hospital().getLevel(),
                        candidate.bedInfo() != null
                                ? new RecommendedHospitalsMessage.BedSummary(
                                        candidate.bedInfo().bedType(),
                                        candidate.bedInfo().totalBedCount(),
                                        candidate.bedInfo().availableBedCount())
                                : null,
                        candidate.score(),
                        candidate.distance()))
                .toList();

        stompEventPublisher.sendToUser(
                ambulanceId.toString(),
                REQUESTED_HOSPITAL_DESTINATION,
                EventType.TRANSFER_REQUESTED_HOSPITALS,
                RecommendedHospitalsMessage.of(items));
    }
}
