package com.aiems.be.hospital.service;

import com.aiems.be.common.domain.Specialty;
import com.aiems.be.hospital.domain.Hospital;
import com.aiems.be.contracts.bed.BedInfoResponse;
import com.aiems.be.contracts.bed.BedInfo;
import com.aiems.be.hospital.bed.service.BedInfoCacheService;
import com.aiems.be.hospital.domain.HospitalSpecialty;
import com.aiems.be.hospital.repository.HospitalRepository;
import com.aiems.be.hospital.repository.HospitalSpecialtyRepository;
import com.aiems.be.hospital.repository.projection.HospitalWithDistance;
import com.aiems.be.hospital.service.policy.HospitalScoringPolicy;
import com.aiems.be.common.domain.PreKTAS;
import com.aiems.be.contracts.ai.SpecialtyConfidence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalRecommendService {

    private static final int EMERGENCY_RADIUS_METERS = 10_000;
    private static final int NON_EMERGENCY_RADIUS_METERS = 2_000;

    private final HospitalRepository hospitalRepository;
    private final HospitalSpecialtyRepository hospitalSpecialtyRepository;
    private final BedInfoCacheService bedInfoCacheService;
    private final HospitalScoringPolicy hospitalScoringPolicy;

    public List<RecommendedHospital> recommend(
            double longitude,
            double latitude,
            PreKTAS preKtas,
            List<SpecialtyConfidence> specialtyConfidences
    ) {
        List<HospitalWithDistance> nearbyHospitals = searchNearby(longitude, latitude, preKtas);

        List<Long> hospitalIds = nearbyHospitals.stream()
                .map(candidate -> candidate.hospital().getId())
                .toList();
        Map<Long, List<Specialty>> specialtyMap = findSpecialties(hospitalIds);

        List<String> hpids = nearbyHospitals.stream()
                .map(candidate -> candidate.hospital().getHpid())
                .toList();
        Map<String, BedInfoResponse.Item> bedInfoMap = bedInfoCacheService.search(hpids);

        Specialty topSpecialty = topSpecialty(specialtyConfidences);

        return nearbyHospitals.stream()
                .map(candidate -> score(candidate, specialtyMap, bedInfoMap, topSpecialty, specialtyConfidences))
                .sorted()
                .toList();
    }

    private List<HospitalWithDistance> searchNearby(double longitude, double latitude, PreKTAS preKtas) {
        double radius = preKtas.isEmergency() ? EMERGENCY_RADIUS_METERS : NON_EMERGENCY_RADIUS_METERS;

        List<HospitalWithDistance> nearby = hospitalRepository.findNearbyHospitalsWithLevel(
                longitude, latitude, radius, preKtas.getHospitalLevel());
        while (nearby.isEmpty()) {
            log.info("반경 {}m 내 병원이 없습니다. 반경을 확대합니다.", radius);
            radius *= 2;
            nearby = hospitalRepository.findNearbyHospitalsWithLevel(
                    longitude, latitude, radius, preKtas.getHospitalLevel());
        }
        return nearby;
    }

    private Map<Long, List<Specialty>> findSpecialties(List<Long> hospitalIds) {
        return hospitalSpecialtyRepository.findAllByHospitalIdIn(hospitalIds).stream()
                .collect(Collectors.groupingBy(
                        specialty -> specialty.getHospital().getId(),
                        Collectors.mapping(HospitalSpecialty::getSpecialty, Collectors.toList())));
    }

    private Specialty topSpecialty(List<SpecialtyConfidence> specialtyConfidences) {
        return specialtyConfidences.stream()
                .max(SpecialtyConfidence::compareTo)
                .orElseThrow(() -> new IllegalStateException("진료과목 신뢰도 리스트가 비어있습니다."))
                .specialty();
    }

    private RecommendedHospital score(
            HospitalWithDistance candidate,
            Map<Long, List<Specialty>> specialtyMap,
            Map<String, BedInfoResponse.Item> bedInfoMap,
            Specialty topSpecialty,
            List<SpecialtyConfidence> specialtyConfidences
    ) {
        Hospital hospital = candidate.hospital();
        List<Specialty> specialties = specialtyMap.getOrDefault(hospital.getId(), List.of());

        BedInfoResponse.Item item = bedInfoMap.getOrDefault(hospital.getHpid(), BedInfoResponse.Item.empty());
        BedInfo bedInfo = item.getBedInfo(topSpecialty);

        double score = hospitalScoringPolicy.calculateScore(HospitalScoringPolicy.ScoreContext.builder()
                .hospital(hospital)
                .hospitalSpecialties(specialties)
                .requiredSpecialties(specialtyConfidences)
                .distance(candidate.distance())
                .build());

        return new RecommendedHospital(hospital, candidate.distance(), score, bedInfo);
    }
}
