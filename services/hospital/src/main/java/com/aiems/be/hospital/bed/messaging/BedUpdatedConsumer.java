package com.aiems.be.hospital.bed.messaging;

import com.aiems.be.hospital.config.ServiceRedis;
import com.aiems.be.contracts.bed.BedUpdated;
import com.aiems.be.contracts.messaging.BedMessaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class BedUpdatedConsumer {

    private static final Duration TTL = Duration.ofMinutes(30);

    @ServiceRedis private final StringRedisTemplate stringRedisTemplate;

    @RabbitListener(queues = BedMessaging.QUEUE_BED_UPDATED)
    public void onBedUpdated(BedUpdated event) {
        stringRedisTemplate.opsForValue().set(event.key(), event.payload(), TTL);
    }
}
