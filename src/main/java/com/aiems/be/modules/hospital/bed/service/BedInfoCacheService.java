package com.aiems.be.modules.hospital.bed.service;

import com.aiems.be.config.ServiceRedis;
import com.aiems.be.modules.hospital.bed.client.BedInfoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.aiems.be.modules.hospital.bed.client.BedInfoResponse.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BedInfoCacheService {

    private static final String REALTIME_BED_INFO_PREFIX = "hospital:realtime:bed";

    @ServiceRedis private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public int update(BedInfoResponse realTimeBedInfo) {
        int success = 0;
        for (Entry<String, Item> items : realTimeBedInfo.toItemMap().entrySet()) {
            try {
                String hpid = items.getKey();
                String value = objectMapper.writeValueAsString(items.getValue());

                stringRedisTemplate.opsForValue().set(createRealTimeBedInfoKey(hpid), value);
                success++;
            } catch (JsonProcessingException ex) {
                throw new IllegalArgumentException("실시간 병상 정보 직렬화에 실패했습니다. [hpid=%s]".formatted(items.getKey()), ex);
            }
        }

        return success;
    }

    public Map<String, Item> search(@NonNull List<String> hpids) {
        List<String> keys = hpids.stream()
                .map(BedInfoCacheService::createRealTimeBedInfoKey)
                .toList();

        List<String> rawValues = stringRedisTemplate.opsForValue().multiGet(keys);

        Map<String, Item> result = new LinkedHashMap<>();

        for (int i = 0; i < hpids.size(); i++) {
            log.info("조회한 실시간 병상 정보: hpid={}, raw={}", hpids.get(i), rawValues.get(i));
            String raw = rawValues.get(i);
            if (raw == null) continue;

            try {
                String json = raw;
                Item item = objectMapper.readValue(json, Item.class);
                result.put(hpids.get(i), item);
            } catch (JsonProcessingException ex) {
                log.warn("실시간 병상 정보 역직렬화 실패 [hpid={}]", hpids.get(i), ex);
            }
        }

        return result;
    }

    private static String createRealTimeBedInfoKey(String hpid) {
        return REALTIME_BED_INFO_PREFIX + ":" + hpid;
    }


}
