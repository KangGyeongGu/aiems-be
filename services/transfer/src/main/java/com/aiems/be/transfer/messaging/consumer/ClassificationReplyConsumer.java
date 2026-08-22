package com.aiems.be.transfer.messaging.consumer;

import com.aiems.be.common.exception.BusinessException;
import com.aiems.be.contracts.ai.PatientClassified;
import com.aiems.be.contracts.messaging.AiMessaging;
import com.aiems.be.transfer.service.TransferRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class ClassificationReplyConsumer {

    private final TransferRequestService transferRequestService;

    @RabbitListener(queues = AiMessaging.QUEUE_CLASSIFICATION_REPLY)
    public void consume(PatientClassified result) {
        if (result == null) {
            log.warn("환자 분류 응답이 null 입니다.");
            return;
        }

        try {
            transferRequestService.onClassified(result);
        } catch (BusinessException e) {
            log.warn("환자 분류 응답 처리 실패. patientId={}, error={}", result.patientId(), e.getMessage());
        }
    }
}
