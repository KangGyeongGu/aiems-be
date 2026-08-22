package com.aiems.be.ai.messaging;

import com.aiems.be.common.domain.Specialty;
import com.aiems.be.common.domain.PreKTAS;
import com.aiems.be.contracts.ai.PatientClassified;
import com.aiems.be.contracts.ai.SpecialtyConfidence;
import com.aiems.be.contracts.ai.TransferRequested;
import com.aiems.be.contracts.messaging.AiMessaging;
import lombok.RequiredArgsConstructor;
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

    @RabbitListener(queues = AiMessaging.QUEUE_CLASSIFICATION)
    public void classify(TransferRequested event) {
        List<SpecialtyConfidence> confidences = List.of(
                new SpecialtyConfidence(Specialty.INTERNAL_MEDICINE, 0.82f),
                new SpecialtyConfidence(Specialty.GENERAL_SURGERY, 0.61f));

        PatientClassified result = new PatientClassified(
                event.patientId(),
                event.ambulanceId(),
                confidences,
                PreKTAS.LEVEL_2);

        rabbitTemplate.convertAndSend(
                AiMessaging.EXCHANGE,
                AiMessaging.ROUTING_KEY_CLASSIFICATION_REPLY,
                result);
    }
}
