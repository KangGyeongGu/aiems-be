package com.aiems.be.modules.transfer.consumer;

import com.aiems.be.modules.transfer.service.TransferRecordService;
import com.aiems.be.modules.transfer.web.message.SummaryJobMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.role.consumer.enabled", havingValue = "true")
public class SummaryJobConsumer {

    private final TransferRecordService transferRecordService;
    private final ProcessedMessageStore processedMessageStore;

    @RabbitListener(queues = "${app.rabbitmq.summary-reply-queue}")
    public void consumeSummaryJob(
            SummaryJobMessage resultMessage,
            @Header(name = AmqpHeaders.MESSAGE_ID, required = false) String messageId) {

        if (resultMessage == null) {
            log.warn("AI 응답이 null 입니다.");
            return;
        }

        if (messageId != null && processedMessageStore.isProcessed(messageId)) {
            log.info("이미 처리된 요약 메시지입니다. 건너뜁니다. messageId={}", messageId);
            return;
        }

        transferRecordService.saveTreatmentRecord(resultMessage.ambulanceId(), resultMessage.message());

        if (messageId != null) {
            processedMessageStore.mark(messageId);
        }
    }
}