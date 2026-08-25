package com.aiems.be.bedingestion.client;

import com.aiems.be.bedingestion.client.NationalMedicalCenterProperties;
import com.aiems.be.bedingestion.client.BedInfoRequest;
import com.aiems.be.contracts.bed.BedInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("!local")
public class NationalMedicalCenterClient implements BedInfoClient {

    private static final String EMERGENCY_REALTIME_BED_INFO_URI = "/getEmrrmRltmUsefulSckbdInfoInqire";

    private final RestClient nationalMedicalCenterRestClient;
    private final NationalMedicalCenterProperties properties;

    @Override
    public BedInfoResponse getRealTimeBedInfo(BedInfoRequest request) {
        return nationalMedicalCenterRestClient.get()
                .uri(EMERGENCY_REALTIME_BED_INFO_URI, uriBuilder -> uriBuilder
                        .queryParam("serviceKey", properties.apiKey())
                        .queryParam("stage1", request.stage1())
                        .queryParam("stage2", request.stage2())
                        .queryParam("pageNo", request.pageNo())
                        .queryParam("numOfRows", request.numOfRows())
                        .build())
                .retrieve()
                .body(BedInfoResponse.class);
    }
}
