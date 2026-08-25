package com.aiems.be.bedingestion;

import com.aiems.be.contracts.messaging.BedMessaging;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class BedIngestionConfig {

    @Bean
    public DirectExchange bedExchange() {
        return new DirectExchange(BedMessaging.EXCHANGE);
    }
}
