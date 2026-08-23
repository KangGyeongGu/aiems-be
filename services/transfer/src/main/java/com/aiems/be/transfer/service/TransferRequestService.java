package com.aiems.be.transfer.service;

import com.aiems.be.transfer.client.AmbulanceClient;
import com.aiems.be.transfer.client.HospitalRecommendClient;
import com.aiems.be.contracts.hospital.HospitalRecommendResponse;
import com.aiems.be.transfer.domain.Patient;
import com.aiems.be.contracts.ambulance.AmbulanceSnapshot;
import com.aiems.be.contracts.transfer.RecommendedHospitalsMessage;
import com.aiems.be.contracts.transfer.TransferRequestMessage;
import com.aiems.be.transfer.messaging.NotificationPublisher;
import com.aiems.be.transfer.messaging.TransferMessageMapper;
import com.aiems.be.transfer.messaging.client.AiMessageClient;
import com.aiems.be.contracts.ai.PatientClassified;
import com.aiems.be.contracts.ai.TransferRequested;
import com.aiems.be.transfer.web.request.TransferRequest;
import com.aiems.be.contracts.websocket.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferRequestService {

    private static final int MAX_REQUESTED_HOSPITAL_COUNT = 10;

    private static final String TRANSFER_REQUEST_DESTINATION = "/queue/patient-transfers.requests";
    private static final String REQUESTED_HOSPITAL_DESTINATION = "/queue/patient-transfers.requested-hospitals";

    private final AiMessageClient aiMessageClient;
    private final AmbulanceClient ambulanceClient;
    private final PatientService patientService;
    private final HospitalRecommendClient hospitalRecommendClient;
    private final TransferSnapshotService transferSnapshotService;
    private final NotificationPublisher notificationPublisher;

    @Transactional
    public void request(TransferRequest payload, Long ambulanceId) {
        AmbulanceSnapshot ambulance = ambulanceClient.getSnapshot(ambulanceId);

        Patient patient = patientService.register(payload.toEntity(null, ambulance));

        aiMessageClient.publishClassificationRequest(
                new TransferRequested(patient.getId(), ambulanceId, payload.toAnalysisRequest()));
    }

    @Transactional
    public void onClassified(PatientClassified result) {
        Patient patient = patientService.findById(result.patientId());
        patient.assignPreKtas(result.preKTAS());

        double lon = patient.getLocation().getCoordinates().getX();
        double lat = patient.getLocation().getCoordinates().getY();

        List<HospitalRecommendResponse> candidates = hospitalRecommendClient.recommend(
                        lon, lat, result.preKTAS(), result.specialtyConfidences()).stream()
                .limit(MAX_REQUESTED_HOSPITAL_COUNT)
                .toList();

        notifyHospitals(candidates, patient);
        transferSnapshotService.save(result.ambulanceId(), candidates.stream()
                .map(HospitalRecommendResponse::hospitalId)
                .toList());
        replyToAmbulance(result.ambulanceId(), candidates);
    }

    private void notifyHospitals(List<HospitalRecommendResponse> candidates, Patient patient) {
        candidates.forEach(candidate -> {
            TransferRequestMessage message = TransferMessageMapper.toRequestMessage(patient, candidate.distance());
            notificationPublisher.sendToUser(
                    candidate.hospitalId().toString(),
                    TRANSFER_REQUEST_DESTINATION,
                    EventType.TRANSFER_REQUEST,
                    message);
            log.info("이송 요청 전파: hospitalId={}, score={}", candidate.hospitalId(), candidate.score());
        });
    }

    private void replyToAmbulance(Long ambulanceId, List<HospitalRecommendResponse> candidates) {
        List<RecommendedHospitalsMessage.Item> items = candidates.stream()
                .map(candidate -> new RecommendedHospitalsMessage.Item(
                        candidate.hospitalId(),
                        candidate.name(),
                        null,
                        null,
                        candidate.bedInfo() != null
                                ? new RecommendedHospitalsMessage.BedSummary(
                                        candidate.bedInfo().bedType(),
                                        candidate.bedInfo().totalBedCount(),
                                        candidate.bedInfo().availableBedCount())
                                : null,
                        candidate.score(),
                        candidate.distance()))
                .toList();

        notificationPublisher.sendToUser(
                ambulanceId.toString(),
                REQUESTED_HOSPITAL_DESTINATION,
                EventType.TRANSFER_REQUESTED_HOSPITALS,
                RecommendedHospitalsMessage.of(items));
    }
}
