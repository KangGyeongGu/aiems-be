package com.aiems.be.transfer.messaging.consumer;

import com.aiems.be.contracts.ai.SummaryReportMessage;
import com.aiems.be.contracts.messaging.AiMessaging;
import com.aiems.be.transfer.service.TransferRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class SummaryReplyConsumer {

    private final TransferRecordService transferRecordService;
    private final ProcessedMessageStore processedMessageStore;

    @RabbitListener(queues = AiMessaging.QUEUE_SUMMARY_REPLY)
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
