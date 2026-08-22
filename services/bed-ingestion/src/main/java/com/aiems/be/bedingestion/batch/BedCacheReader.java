package com.aiems.be.bedingestion.batch;

import com.aiems.be.bedingestion.client.NationalMedicalCenterClient;
import com.aiems.be.bedingestion.client.BedInfoRequest;
import com.aiems.be.contracts.bed.BedInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.lang.Nullable;

import java.util.Iterator;

@RequiredArgsConstructor
public class BedCacheReader implements ItemReader<BedInfoResponse.Item> {

    private final NationalMedicalCenterClient client;
    private Iterator<BedInfoResponse.Item> iterator;

    @Nullable
    @Override
    public BedInfoResponse.Item read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (iterator == null) {
            BedInfoResponse response = client.getRealTimeBedInfo(BedInfoRequest.ofDefault());
            iterator = response.toItemMap().values().iterator();
        }

        return iterator.hasNext() ? iterator.next() : null;
    }
}
