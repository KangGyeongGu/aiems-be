package com.aiems.be.modules.transfer.messaging.mock;

import com.aiems.be.modules.transfer.messaging.contract.SummaryJobMessage;
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

    private static final String MOCK_SUMMARY_REPLY = """
            {"id":"mock-summary-1","object":"summary","created":0,"model":"mock-ai",\
            "treatment_info":{"medications_given":["니트로글리세린"],"other_treatments":["산소 공급"]},\
            "summary_html":"<p>환자 상태 안정, 응급 처치 완료</p>"}""";

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "${app.rabbitmq.summary-queue}")
    public void summarize(SummaryJobMessage jobMessage, Message raw) {
        SummaryJobMessage reply = new SummaryJobMessage(jobMessage.ambulanceId(), MOCK_SUMMARY_REPLY, null);

        String replyTo = raw.getMessageProperties().getReplyTo();
        String messageId = raw.getMessageProperties().getMessageId();

        rabbitTemplate.convertAndSend("", replyTo, reply, message -> {
            message.getMessageProperties().setMessageId(messageId);
            return message;
        });
    }
}
