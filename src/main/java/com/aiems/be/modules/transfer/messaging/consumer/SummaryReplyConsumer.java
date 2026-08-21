package com.aiems.be.modules.transfer.messaging.consumer;

import com.aiems.be.modules.transfer.messaging.contract.SummaryReportMessage;
import com.aiems.be.modules.transfer.service.TransferRecordService;
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
public class SummaryReplyConsumer {

    private final TransferRecordService transferRecordService;
    private final ProcessedMessageStore processedMessageStore;

    @RabbitListener(queues = "${app.rabbitmq.summary-reply-queue}")
    public void consume(
            SummaryReportMessage reply,
            @Header(name = AmqpHeaders.MESSAGE_ID, required = false) String messageId) {

        if (reply == null) {
            log.warn("AI 요약 응답이 null 입니다.");
            return;
        }

        if (messageId != null && processedMessageStore.isProcessed(messageId)) {
            log.info("이미 처리된 요약 응답입니다. 건너뜁니다. messageId={}", messageId);
            return;
        }

        transferRecordService.saveReport(reply.ambulanceId(), reply.patientId(), reply.reportKey());

        if (messageId != null) {
            processedMessageStore.mark(messageId);
        }
    }
}
