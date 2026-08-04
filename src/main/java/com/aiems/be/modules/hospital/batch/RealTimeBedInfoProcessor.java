package com.aiems.be.modules.hospital.batch;

import com.aiems.be.modules.hospital.client.response.RealTimeBedInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
public class RealTimeBedInfoProcessor implements ItemProcessor<RealTimeBedInfoResponse.Item, RealTimeBedCacheEntry> {

    private static final String REALTIME_BED_INFO_PREFIX = "hospital:realtime:bed";
    private final ObjectMapper objectMapper;

    @Nullable
    @Override
    public RealTimeBedCacheEntry process(@NonNull RealTimeBedInfoResponse.Item item) throws Exception {
        String key = REALTIME_BED_INFO_PREFIX + ":" + item.hpid();
        String value = objectMapper.writeValueAsString(item);

        return new RealTimeBedCacheEntry(key, value);
    }
}
