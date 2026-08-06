package com.aiems.be.modules.transfer.service.result;

import lombok.Builder;

import java.util.List;

@Builder
public record CreateTransferCompleteMessageResult(
        Long ambulanceId,
        List<MessagePlan> messagePlans
) {

    public enum MessageType { ACCEPT, DENY }

    public record MessagePlan(
            Long hospitalId,
            MessageType type
    ) {
        public static MessagePlan accept(Long hospitalId) {
            return new MessagePlan(hospitalId, MessageType.ACCEPT);
        }

        public static MessagePlan deny(Long hospitalId) {
            return new MessagePlan(hospitalId, MessageType.DENY);
        }
    }
}