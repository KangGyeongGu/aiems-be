package com.aiems.be.bedingestion.client;

import com.aiems.be.contracts.bed.BedInfoResponse;

public interface BedInfoClient {

    BedInfoResponse getRealTimeBedInfo(BedInfoRequest request);
}
