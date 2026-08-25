package com.aiems.be.contracts.messaging;

public final class AiMessaging {

    private AiMessaging() {
    }

    public static final String EXCHANGE = "ai_exchange";

    public static final String ROUTING_KEY_CLASSIFICATION = "patient.classification";
    public static final String ROUTING_KEY_CLASSIFICATION_REPLY = "patient.classification_reply";
    public static final String ROUTING_KEY_SUMMARY = "patient.summary";
    public static final String ROUTING_KEY_SUMMARY_REPLY = "patient.summary_reply";

    public static final String QUEUE_CLASSIFICATION = "patient_classification";
    public static final String QUEUE_CLASSIFICATION_REPLY = "classification_reply_queue";
    public static final String QUEUE_SUMMARY = "patient_summary";
    public static final String QUEUE_SUMMARY_REPLY = "summary_reply_queue";
}
