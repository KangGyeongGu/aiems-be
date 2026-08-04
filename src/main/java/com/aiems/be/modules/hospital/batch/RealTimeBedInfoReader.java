package com.aiems.be.modules.hospital.batch;

import com.aiems.be.modules.hospital.client.NationalMedicalCenterClient;
import com.aiems.be.modules.hospital.client.request.RealTimeBedInfoRequest;
import com.aiems.be.modules.hospital.client.response.RealTimeBedInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.lang.Nullable;

import java.util.Iterator;

@RequiredArgsConstructor
public class RealTimeBedInfoReader implements ItemReader<RealTimeBedInfoResponse.Item> {

    private final NationalMedicalCenterClient client;
    private Iterator<RealTimeBedInfoResponse.Item> iterator;

    @Nullable
    @Override
    public RealTimeBedInfoResponse.Item read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (iterator == null) {
            RealTimeBedInfoResponse response = client.getRealTimeBedInfo(RealTimeBedInfoRequest.ofDefault());
            iterator = response.toItemMap().values().iterator();
        }

        return iterator.hasNext() ? iterator.next() : null;
    }
}
