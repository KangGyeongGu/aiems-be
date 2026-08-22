package com.aiems.be.bedingestion.batch;

import com.aiems.be.contracts.bed.BedUpdated;
import com.aiems.be.contracts.messaging.BedMessaging;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class BedCacheWriter implements ItemWriter<BedCacheEntry> {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void write(Chunk<? extends BedCacheEntry> chunk) {
        chunk.getItems().forEach(entry ->
                rabbitTemplate.convertAndSend(BedMessaging.EXCHANGE, BedMessaging.ROUTING_KEY_BED_UPDATED,
                        new BedUpdated(entry.key(), entry.value())));
    }
}
