package com.aiems.be.bedingestion.client;


public record BedInfoRequest(
        String stage1,

        String stage2,

        Integer pageNo,

        Integer numOfRows
) {
    private static final int DEFAULT_PAGE_NO = 1;
    private static final int DEFAULT_NUM_OF_ROWS = 1000;

    public static BedInfoRequest ofDefault() {
        return new BedInfoRequest("", "", DEFAULT_PAGE_NO, DEFAULT_NUM_OF_ROWS);
    }
}
