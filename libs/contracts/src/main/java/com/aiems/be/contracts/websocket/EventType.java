package com.aiems.be.contracts.websocket;

public enum EventType {
    CONNECTED,
    ERROR,
    HEARTBEAT,
    TIME_SYNC,
    NOTIFICATION,
    MESSAGE,
    TRANSFER_REQUEST,
    TRANSFER_REQUESTED_HOSPITALS,
    TRANSFER_RESPONSE,
    TRANSFER_COMPLETE,
    TRANSFER_DONE
}
