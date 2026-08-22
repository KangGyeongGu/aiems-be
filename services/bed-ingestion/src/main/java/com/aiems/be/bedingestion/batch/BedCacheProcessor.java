package com.aiems.be.bedingestion.batch;

import com.aiems.be.contracts.bed.BedInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
public class BedCacheProcessor implements ItemProcessor<BedInfoResponse.Item, BedCacheEntry> {

    private static final String REALTIME_BED_INFO_PREFIX = "hospital:realtime:bed";
    private final ObjectMapper objectMapper;

    @Nullable
    @Override
    public BedCacheEntry process(@NonNull BedInfoResponse.Item item) throws Exception {
        String key = REALTIME_BED_INFO_PREFIX + ":" + item.hpid();
        String value = objectMapper.writeValueAsString(item);

        return new BedCacheEntry(key, value);
    }
}
