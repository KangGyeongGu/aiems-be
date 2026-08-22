package com.aiems.be.hospital.web.controller;

import com.aiems.be.common.domain.PreKTAS;
import com.aiems.be.contracts.bed.BedInfo;
import com.aiems.be.hospital.service.HospitalRecommendService;
import com.aiems.be.hospital.service.RecommendedHospital;
import com.aiems.be.contracts.ai.SpecialtyConfidence;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/hospitals/recommendations")
public class HospitalRecommendController {

    private final HospitalRecommendService hospitalRecommendService;

    @PostMapping
    public List<Response> recommend(@RequestBody Request request) {
        return hospitalRecommendService.recommend(
                        request.longitude(),
                        request.latitude(),
                        request.preKtas(),
                        request.specialtyConfidences())
                .stream()
                .map(Response::from)
                .toList();
    }

    public record Request(
            double longitude,
            double latitude,
            PreKTAS preKtas,
            List<SpecialtyConfidence> specialtyConfidences
    ) {
    }

    public record Response(
            Long hospitalId,
            String name,
            String hpid,
            Double distance,
            double score,
            BedInfo bedInfo
    ) {
        public static Response from(RecommendedHospital recommended) {
            return new Response(
                    recommended.hospital().getId(),
                    recommended.hospital().getName(),
                    recommended.hospital().getHpid(),
                    recommended.distance(),
                    recommended.score(),
                    recommended.bedInfo());
        }
    }
}
