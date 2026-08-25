package com.aiems.be.bedingestion.client;

import com.aiems.be.contracts.bed.BedInfoResponse;
import com.aiems.be.contracts.bed.BedInfoResponse.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@Profile("local")
public class MockBedInfoClient implements BedInfoClient {

    private static final String HPID_RESOURCE = "mock/bed-hpids.txt";

    private final ObjectMapper objectMapper;
    private final List<String> hpids;

    public MockBedInfoClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.hpids = loadHpids();
        log.info("Mock 병상 소스 초기화: hpid {}건", hpids.size());
    }

    @Override
    public BedInfoResponse getRealTimeBedInfo(BedInfoRequest request) {
        List<Item> items = new ArrayList<>(hpids.size());
        for (String hpid : hpids) {
            items.add(mockItem(hpid));
        }
        int size = items.size();
        return new BedInfoResponse(
                new BedInfoResponse.Header("00", "NORMAL SERVICE."),
                new BedInfoResponse.Body(items, 1, size, size));
    }

    private Item mockItem(String hpid) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("hpid", hpid);
        fields.put("hvidate", "202608010000");

        putBed(fields, "hvec", "hvs01");   // 일반병동
        putBed(fields, "hv2", "hvs06");    // 내과 중환자실
        putBed(fields, "hv3", "hvs07");    // 외과 중환자실
        putBed(fields, "hvcc", "hvs11");   // 신경과 중환자실
        putBed(fields, "hvccc", "hvs16");  // 흉부외과 중환자실
        putBed(fields, "hv6", "hvs12");    // 신경외과 중환자실
        putBed(fields, "hv33", "hvs10");   // 소아 중환자실
        putBed(fields, "hv40", "hvs24");   // 정신과 폐쇄병동
        putBed(fields, "hv42", "hvs26");   // 분만실

        return objectMapper.convertValue(fields, Item.class);
    }

    private void putBed(Map<String, Object> fields, String availableCode, String totalCode) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int available = random.nextInt(0, 21);
        int total = available + random.nextInt(0, 21);
        fields.put(availableCode, String.valueOf(available));
        fields.put(totalCode, total);
    }

    private List<String> loadHpids() {
        List<String> loaded = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource(HPID_RESOURCE).getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String hpid = line.trim();
                if (!hpid.isEmpty()) {
                    loaded.add(hpid);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Mock 병상 hpid 리소스 로드 실패: " + HPID_RESOURCE, e);
        }
        return loaded;
    }
}
