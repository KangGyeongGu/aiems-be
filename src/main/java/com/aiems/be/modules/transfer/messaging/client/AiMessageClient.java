package com.aiems.be.modules.transfer.messaging.client;

import com.aiems.be.common.exception.BusinessException;
import com.aiems.be.modules.transfer.exception.TransferErrorCode;
import com.aiems.be.modules.transfer.messaging.contract.PatientAnalysisRequest;
import com.aiems.be.modules.transfer.messaging.contract.PatientAnalysisResult;
import com.aiems.be.modules.transfer.messaging.contract.SummaryJobMessage;
import com.aiems.be.rabbitmq.config.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AiMessageClient {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties properties;

    public PatientAnalysisResult requestClassification(PatientAnalysisRequest request) {
        PatientAnalysisResult result = rabbitTemplate.convertSendAndReceiveAsType(
                properties.exchange(),
                properties.routingKeyClassification(),
                request,
                new ParameterizedTypeReference<PatientAnalysisResult>() {});

        if (result == null) {
            throw new BusinessException(TransferErrorCode.ANALYSIS_TIMEOUT);
        }
        return result;
    }

    public void publishSummaryRequest(SummaryJobMessage jobMessage) {
        String messageId = UUID.randomUUID().toString();

        rabbitTemplate.convertAndSend(
                properties.exchange(),
                properties.routingKeySummary(),
                jobMessage,
                message -> {
                    message.getMessageProperties().setReplyTo(properties.summaryReplyQueue());
                    message.getMessageProperties().setMessageId(messageId);
                    return message;
                },
                new CorrelationData(messageId)
        );
    }
}
