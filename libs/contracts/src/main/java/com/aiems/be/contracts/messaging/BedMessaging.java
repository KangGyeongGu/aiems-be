package com.aiems.be.contracts.messaging;

public final class BedMessaging {

    private BedMessaging() {
    }

    public static final String EXCHANGE = "bed_exchange";

    public static final String ROUTING_KEY_BED_UPDATED = "bed.updated";

    public static final String QUEUE_BED_UPDATED = "hospital_bed_updated";
}
