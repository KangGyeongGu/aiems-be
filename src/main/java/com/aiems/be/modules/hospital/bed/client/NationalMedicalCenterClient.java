package com.aiems.be.modules.hospital.bed.client;

import com.aiems.be.modules.hospital.bed.client.NationalMedicalCenterProperties;
import com.aiems.be.modules.hospital.bed.client.BedInfoRequest;
import com.aiems.be.modules.hospital.bed.client.BedInfoResponse;
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
