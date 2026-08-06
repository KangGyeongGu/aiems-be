package com.aiems.be.modules.hospital.bed.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Profile("local")
@RestController
@RequestMapping("/mock/nmc/ErmctInfoInqireService")
public class NationalMedicalCenterMockController {

    private static final String CANNED_XML_LOCATION = "mock/nmc-realtime-bed-info.xml";

    @GetMapping(
            value = "/getEmrrmRltmUsefulSckbdInfoInqire",
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public String getRealTimeBedInfo() {
        log.info("[MOCK] 국립중앙의료원 실시간 가용병상 조회 — 고정 XML 응답");
        return readCannedXml();
    }

    private String readCannedXml() {
        try {
            ClassPathResource resource = new ClassPathResource(CANNED_XML_LOCATION);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "mock NMC 고정 XML 을 읽는데 실패하였습니다. [location=%s]".formatted(CANNED_XML_LOCATION), e);
        }
    }
}
