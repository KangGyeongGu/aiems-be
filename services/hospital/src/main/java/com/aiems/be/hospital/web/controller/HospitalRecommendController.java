package com.aiems.be.hospital.web.controller;

import com.aiems.be.contracts.hospital.HospitalRecommendRequest;
import com.aiems.be.contracts.hospital.HospitalRecommendResponse;
import com.aiems.be.hospital.service.HospitalRecommendService;
import com.aiems.be.hospital.service.RecommendedHospital;
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
    public List<HospitalRecommendResponse> recommend(@RequestBody HospitalRecommendRequest request) {
        return hospitalRecommendService.recommend(
                        request.longitude(),
                        request.latitude(),
                        request.preKtas(),
                        request.specialtyConfidences())
                .stream()
                .map(HospitalRecommendController::toResponse)
                .toList();
    }

    private static HospitalRecommendResponse toResponse(RecommendedHospital recommended) {
        return new HospitalRecommendResponse(
                recommended.hospital().getId(),
                recommended.hospital().getName(),
                recommended.hospital().getHpid(),
                recommended.distance(),
                recommended.score(),
                recommended.bedInfo());
    }
}
