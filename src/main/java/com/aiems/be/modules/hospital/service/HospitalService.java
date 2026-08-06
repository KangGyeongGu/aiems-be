package com.aiems.be.modules.hospital.service;

import com.aiems.be.modules.hospital.domain.Hospital;
import com.aiems.be.modules.hospital.domain.Specialty;
import com.aiems.be.modules.hospital.repository.HospitalRepository;
import com.aiems.be.modules.hospital.repository.HospitalSpecialtyRepository;
import com.aiems.be.modules.hospital.repository.dto.HospitalWithDistanceDto;
import com.aiems.be.modules.hospital.repository.dto.HospitalWithSpecialtyDto;
import com.aiems.be.modules.hospital.service.command.HospitalRecommendCommand;
import com.aiems.be.modules.hospital.service.policy.HospitalScoringPolicy;
import com.aiems.be.modules.hospital.service.result.BedInfo;
import com.aiems.be.modules.hospital.service.result.HospitalRecommendResult;
import com.aiems.be.modules.hospital.service.result.ScoredHospital;
import com.aiems.be.modules.hospital.client.response.RealTimeBedInfoResponse;
import com.aiems.be.modules.patient.service.result.SpecialtyConfidence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class HospitalService {

    private final HospitalScoringPolicy hospitalScoringPolicy;
    private final HospitalRepository hospitalRepository;
    private final HospitalSpecialtyRepository hospitalSpecialtyRepository;
    private final RealTimeBedInfoService realTimeBedInfoService;

    // 병원 추천: 공간 쿼리로 후보를 모으고, 진료과 적합도·실시간 병상·거리·등급으로 점수화해 정렬한다
    public HospitalRecommendResult recommendHospital(HospitalRecommendCommand command) {
        List<HospitalWithDistanceDto> nearbyHospitals = searchNearbyHospitals(command);

        List<Long> nearbyHospitalIds = nearbyHospitals.stream()
                .map(dto -> dto.getHospital().getId())
                .toList();

        List<HospitalWithSpecialtyDto> withSpecialties = hospitalSpecialtyRepository.findHospitalsWithSpecialties(nearbyHospitalIds);

        Map<Long, List<Specialty>> specialtyMap = withSpecialties.stream()
                .collect(Collectors.toMap(
                        dto -> dto.getHospital().getId(),
                        HospitalWithSpecialtyDto::getSpecialties
                ));

        List<String> hpids = nearbyHospitals.stream()
                .map(h -> h.getHospital().getHpid())
                .toList();

        Map<String, RealTimeBedInfoResponse.Item> bedInfoMap = realTimeBedInfoService.search(hpids);

        Specialty topSpecialty = command.getTopSpecialty();
        List<ScoredHospital> scoredHospitals = nearbyHospitals.stream()
                .map(dto -> {
                    Long hospitalId = dto.getHospital().getId();
                    List<Specialty> specialties = specialtyMap.getOrDefault(hospitalId, List.of());

                    RealTimeBedInfoResponse.Item item = bedInfoMap.getOrDefault(dto.getHospital().getHpid(), RealTimeBedInfoResponse.Item.empty());
                    BedInfo bedInfo = item.getBedInfo(topSpecialty);

                    return scoreHospitals(dto, bedInfo, specialties, command.specialtyConfidences());
                })
                .sorted()
                .collect(Collectors.toList());

        return HospitalRecommendResult.builder()
                .hospitals(scoredHospitals)
                .build();
    }

    // 반경 내 병원이 없으면 반경을 2배씩 확대해 재검색한다
    private List<HospitalWithDistanceDto> searchNearbyHospitals(HospitalRecommendCommand command) {
        List<HospitalWithDistanceDto> nearbyHospitals = hospitalRepository.findNearbyHospitalsWithLevel(command.longitude(), command.latitude(), command.radius(), command.preKTAS().getHospitalLevel());
        double radius = command.radius();
        while (nearbyHospitals.isEmpty()) {
            log.info("반경 {}m 내 병원이 없습니다. 반경을 확대합니다.", radius);
            radius *= 2;
            nearbyHospitals = hospitalRepository.findNearbyHospitalsWithLevel(command.longitude(), command.latitude(), radius, command.preKTAS().getHospitalLevel());
        }
        return nearbyHospitals;
    }

    private ScoredHospital scoreHospitals(HospitalWithDistanceDto hospitalWithDistance, BedInfo bedInfo, List<Specialty> specialties, List<SpecialtyConfidence> requiredSpecialties) {
        Hospital hospital = hospitalWithDistance.getHospital();
        Double distance = hospitalWithDistance.getDistance();

        HospitalScoringPolicy.ScoreContext scoreContext = HospitalScoringPolicy.ScoreContext.builder()
                .hospital(hospital)
                .hospitalSpecialties(specialties)
                .requiredSpecialties(requiredSpecialties)
                .distance(distance)
                .build();

        double score = hospitalScoringPolicy.calculateScore(scoreContext);

        return ScoredHospital.builder()
                .hospital(hospital)
                .distance(distance)
                .bedInfo(bedInfo)
                .score(score)
                .build();
    }
}