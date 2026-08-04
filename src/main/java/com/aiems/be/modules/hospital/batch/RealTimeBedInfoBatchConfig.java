package com.aiems.be.modules.hospital.batch;

import com.aiems.be.config.ServiceRedis;
import com.aiems.be.modules.hospital.client.NationalMedicalCenterClient;
import com.aiems.be.modules.hospital.client.response.RealTimeBedInfoResponse;
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
public class RealTimeBedInfoBatchConfig {

    private static final int CHUNK_SIZE = 100;
    private static final int RETRY_LIMIT = 3;
    private static final int SKIP_LIMIT = 50;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final NationalMedicalCenterClient client;
    private final ObjectMapper objectMapper;

    @Bean
    public Job realTimeBedInfoJob(Step realTimeBedInfoStep) {
        return new JobBuilder("realTimeBedInfoJob", jobRepository)
                .start(realTimeBedInfoStep)
                .build();
    }

    @Bean
    @StepScope
    public RealTimeBedInfoReader realTimeBedInfoReader() {
        return new RealTimeBedInfoReader(client);
    }

    @Bean
    public Step realTimeBedInfoStep(
            RealTimeBedInfoReader realTimeBedInfoReader,
            @ServiceRedis StringRedisTemplate serviceStringRedisTemplate
    ) {
        return new StepBuilder("realTimeBedInfoStep", jobRepository)
                .<RealTimeBedInfoResponse.Item, RealTimeBedCacheEntry>chunk(CHUNK_SIZE, transactionManager)
                .reader(realTimeBedInfoReader)
                .processor(new RealTimeBedInfoProcessor(objectMapper))
                .writer(new RealTimeBedInfoWriter(serviceStringRedisTemplate))
                .faultTolerant()

                .retry(RedisConnectionFailureException.class)
                .retryLimit(RETRY_LIMIT)

                .skip(JsonRpcException.class)
                .skipLimit(SKIP_LIMIT)

                .build();
    }
}
