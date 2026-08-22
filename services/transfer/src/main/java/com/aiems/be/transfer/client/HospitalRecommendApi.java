package com.aiems.be.transfer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "hospitalRecommend", url = "${app.services.hospital-url}")
public interface HospitalRecommendApi {

    @PostMapping("/internal/hospitals/recommendations")
    List<HospitalRecommendClient.RecommendedHospital> recommend(HospitalRecommendClient.Request request);
}
