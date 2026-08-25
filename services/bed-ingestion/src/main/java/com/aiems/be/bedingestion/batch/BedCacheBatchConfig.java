package com.aiems.be.bedingestion.batch;

import com.aiems.be.bedingestion.client.BedInfoClient;
import com.aiems.be.contracts.bed.BedInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.tools.jsonrpc.JsonRpcException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BedCacheBatchConfig {

    private static final int CHUNK_SIZE = 100;
    private static final int SKIP_LIMIT = 50;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final BedInfoClient client;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

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
    public Step bedCacheStep(BedCacheReader bedCacheReader) {
        return new StepBuilder("bedCacheStep", jobRepository)
                .<BedInfoResponse.Item, BedCacheEntry>chunk(CHUNK_SIZE, transactionManager)
                .reader(bedCacheReader)
                .processor(new BedCacheProcessor(objectMapper))
                .writer(new BedCacheWriter(rabbitTemplate))
                .faultTolerant()
                .skip(JsonRpcException.class)
                .skipLimit(SKIP_LIMIT)
                .build();
    }
}
