package com.aiems.be.modules.transfer.messaging.mock;

import com.aiems.be.common.domain.Specialty;
import com.aiems.be.modules.transfer.domain.PreKTAS;
import com.aiems.be.modules.transfer.messaging.contract.PatientAnalysisRequest;
import com.aiems.be.modules.transfer.messaging.contract.PatientAnalysisResult;
import com.aiems.be.modules.transfer.messaging.contract.SpecialtyConfidence;
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
        PatientAnalysisResult reply = new PatientAnalysisResult(
                List.of(new SpecialtyConfidence(Specialty.INTERNAL_MEDICINE, 0.82f),
                        new SpecialtyConfidence(Specialty.GENERAL_SURGERY, 0.61f)),
                PreKTAS.LEVEL_2);

        String replyTo = raw.getMessageProperties().getReplyTo();
        String correlationId = raw.getMessageProperties().getCorrelationId();

        rabbitTemplate.convertAndSend("", replyTo, reply, message -> {
            message.getMessageProperties().setCorrelationId(correlationId);
            return message;
        });
    }
}
