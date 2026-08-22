package com.aiems.be.ai.messaging;

import com.aiems.be.contracts.ai.SummaryJobMessage;
import com.aiems.be.contracts.ai.SummaryReportMessage;
import com.aiems.be.contracts.messaging.AiMessaging;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@RequiredArgsConstructor
public class AiSummaryMockConsumer {

    private static final String MOCK_REPORT_KEY = "report/mock/mock-report.pdf";

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = AiMessaging.QUEUE_SUMMARY)
    public void summarize(SummaryJobMessage jobMessage, Message raw) {
        SummaryReportMessage reply = new SummaryReportMessage(
                jobMessage.ambulanceId(), jobMessage.patient().id(), MOCK_REPORT_KEY);

        String replyTo = raw.getMessageProperties().getReplyTo();
        String messageId = raw.getMessageProperties().getMessageId();

        rabbitTemplate.convertAndSend("", replyTo, reply, message -> {
            message.getMessageProperties().setMessageId(messageId);
            return message;
        });
    }
}
