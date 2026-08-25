package com.aiems.be.transfer.messaging.client;

import com.aiems.be.contracts.ai.SummaryJobMessage;
import com.aiems.be.contracts.ai.TransferRequested;
import com.aiems.be.contracts.messaging.AiMessaging;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AiMessageClient {

    private final RabbitTemplate rabbitTemplate;

    public void publishClassificationRequest(TransferRequested event) {
        rabbitTemplate.convertAndSend(
                AiMessaging.EXCHANGE,
                AiMessaging.ROUTING_KEY_CLASSIFICATION,
                event);
    }

    public void publishSummaryRequest(SummaryJobMessage jobMessage) {
        String messageId = UUID.randomUUID().toString();

        rabbitTemplate.convertAndSend(
                AiMessaging.EXCHANGE,
                AiMessaging.ROUTING_KEY_SUMMARY,
                jobMessage,
                message -> {
                    message.getMessageProperties().setReplyTo(AiMessaging.QUEUE_SUMMARY_REPLY);
                    message.getMessageProperties().setMessageId(messageId);
                    return message;
                },
                new CorrelationData(messageId)
        );
    }
}
