package com.aiems.be.modules.hospital.bed.batch;

import com.aiems.be.config.ServiceRedis;
import com.aiems.be.modules.hospital.bed.client.NationalMedicalCenterClient;
import com.aiems.be.modules.hospital.bed.client.BedInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.tools.jsonrpc.JsonRpcException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.role.batch.enabled", havingValue = "true")
public class BedCacheBatchConfig {

    private static final int CHUNK_SIZE = 100;
    private static final int RETRY_LIMIT = 3;
    private static final int SKIP_LIMIT = 50;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final NationalMedicalCenterClient client;
    private final ObjectMapper objectMapper;

    @Bean
    public Job bedCacheJob(Step bedCacheStep) {
        return new JobBuilder("bedCacheJob", jobRepository)
                .start(bedCacheStep)
                .build();
    }

    @Bean
    @StepScope
    public BedCacheReader bedCacheReader() {
        return new BedCacheReader(client);
    }

    @Bean
    public Step bedCacheStep(
            BedCacheReader bedCacheReader,
            @ServiceRedis StringRedisTemplate serviceStringRedisTemplate
    ) {
        return new StepBuilder("bedCacheStep", jobRepository)
                .<BedInfoResponse.Item, BedCacheEntry>chunk(CHUNK_SIZE, transactionManager)
                .reader(bedCacheReader)
                .processor(new BedCacheProcessor(objectMapper))
                .writer(new BedCacheWriter(serviceStringRedisTemplate))
                .faultTolerant()

                .retry(RedisConnectionFailureException.class)
                .retryLimit(RETRY_LIMIT)

                .skip(JsonRpcException.class)
                .skipLimit(SKIP_LIMIT)

                .build();
    }
}
