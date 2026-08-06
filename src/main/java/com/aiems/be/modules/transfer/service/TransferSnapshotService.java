package com.aiems.be.modules.transfer.service;

import com.aiems.be.modules.transfer.service.command.TransferRequestSnapshotCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.aiems.be.config.ServiceRedis;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransferSnapshotService {

    private static final String TRANSFER_REQUEST_SNAPSHOT_KEY_PREFIX = "transfer_request_snapshot:";

    @ServiceRedis private final StringRedisTemplate redisTemplate;

    @Transactional
    public void saveTransferRequestSnapshot(TransferRequestSnapshotCommand command) {
        String key = getTransferRequestSnapshotKey(command.ambulanceId());
        redisTemplate.delete(key);

        String[] requestedHospitals = command.hospitalIds()
                .stream()
                .map(String::valueOf)
                .toArray(String[]::new);

        redisTemplate.opsForSet().add(key, requestedHospitals);
    }

    public List<Long> getTransferRequestSnapshot(Long ambulanceId) {
        List<Long> requestedHospitals = redisTemplate.opsForSet()
                .members(getTransferRequestSnapshotKey(ambulanceId))
                .stream()
                .map(Long::valueOf)
                .toList();

        redisTemplate.expire(getTransferRequestSnapshotKey(ambulanceId), Duration.ofMinutes(30));
        return requestedHospitals;
    }

    private String getTransferRequestSnapshotKey(Long ambulanceId) {
        return TRANSFER_REQUEST_SNAPSHOT_KEY_PREFIX + ambulanceId;
    }

}