package com.aiems.be.modules.transfer.consumer;

import com.aiems.be.modules.hospital.domain.Specialty;
import com.aiems.be.modules.patient.domain.PreKTAS;
import com.aiems.be.modules.patient.service.request.PatientAnalysisRequest;
import com.aiems.be.modules.patient.service.result.PatientAnalysisResult;
import com.aiems.be.modules.patient.service.result.SpecialtyConfidence;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("local")
@RequiredArgsConstructor
public class AiClassificationMockConsumer {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "${app.rabbitmq.classification-queue}")
    public void classify(PatientAnalysisRequest request, Message raw) {
        PatientAnalysisResult reply = PatientAnalysisResult.builder()
                .preKTAS(PreKTAS.LEVEL_2)
                .specialtyConfidences(List.of(
                        SpecialtyConfidence.builder().specialty(Specialty.INTERNAL_MEDICINE).confidence(0.82f).build(),
                        SpecialtyConfidence.builder().specialty(Specialty.GENERAL_SURGERY).confidence(0.61f).build()))
                .build();

        String replyTo = raw.getMessageProperties().getReplyTo();
        String correlationId = raw.getMessageProperties().getCorrelationId();

        rabbitTemplate.convertAndSend("", replyTo, reply, message -> {
            message.getMessageProperties().setCorrelationId(correlationId);
            return message;
        });
    }
}
