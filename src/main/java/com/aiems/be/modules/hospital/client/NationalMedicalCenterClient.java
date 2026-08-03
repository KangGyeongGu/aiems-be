package com.aiems.be.modules.hospital.client;

import com.aiems.be.modules.hospital.client.config.NationalMedicalCenterProperties;
import com.aiems.be.modules.hospital.client.request.RealTimeBedInfoRequest;
import com.aiems.be.modules.hospital.client.response.RealTimeBedInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class NationalMedicalCenterClient {

    private static final String EMERGENCY_REALTIME_BED_INFO_URI = "/getEmrrmRltmUsefulSckbdInfoInqire";

    private final RestClient nationalMedicalCenterRestClient;
    private final NationalMedicalCenterProperties properties;

    public RealTimeBedInfoResponse getRealTimeBedInfo(RealTimeBedInfoRequest request) {
        return nationalMedicalCenterRestClient.get()
                .uri(EMERGENCY_REALTIME_BED_INFO_URI, uriBuilder -> uriBuilder
                        .queryParam("serviceKey", properties.apiKey())
                        .queryParam("stage1", request.stage1())
                        .queryParam("stage2", request.stage2())
                        .queryParam("pageNo", request.pageNo())
                        .queryParam("numOfRows", request.numOfRows())
                        .build())
                .retrieve()
                .body(RealTimeBedInfoResponse.class);
    }
}
