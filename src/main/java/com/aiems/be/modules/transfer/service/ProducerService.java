package com.aiems.be.modules.transfer.service;

import com.aiems.be.modules.patient.service.request.PatientAnalysisRequest;
import com.aiems.be.modules.patient.service.result.PatientAnalysisResult;
import com.aiems.be.modules.transfer.web.message.SummaryJobMessage;
import com.aiems.be.rabbitmq.config.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties properties;

    public PatientAnalysisResult sendAnalysisMessage(PatientAnalysisRequest request) {
        return rabbitTemplate.convertSendAndReceiveAsType(
                properties.exchange(),
                properties.routingKeyClassification(),
                request,
                new ParameterizedTypeReference<PatientAnalysisResult>() {}
        );
    }

    public void sendSummaryJobAsync(SummaryJobMessage jobMessage) {
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
