package com.aiems.be.modules.hospital.client.request;

import lombok.Builder;

@Builder
public record RealTimeBedInfoRequest(
        String stage1,

        String stage2,

        Integer pageNo,

        Integer numOfRows
) {
    private static final int DEFAULT_PAGE_NO = 1;
    private static final int DEFAULT_NUM_OF_ROWS = 1000;

    public static RealTimeBedInfoRequest ofDefault() {
        return RealTimeBedInfoRequest.builder()
                .stage1("")
                .stage2("")
                .pageNo(DEFAULT_PAGE_NO)
                .numOfRows(DEFAULT_NUM_OF_ROWS)
                .build();
    }
}
