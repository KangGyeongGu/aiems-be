package com.aiems.be.modules.hospital.bed.batch;

import com.aiems.be.modules.hospital.bed.client.NationalMedicalCenterClient;
import com.aiems.be.modules.hospital.bed.client.BedInfoRequest;
import com.aiems.be.modules.hospital.bed.client.BedInfoResponse;
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
