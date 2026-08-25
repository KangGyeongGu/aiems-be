package com.aiems.be.bedingestion.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BedCacheJobScheduler {

    private static final String EVERY_10_MINUTES = "0 */10 * * * *";

    private final JobLauncher jobLauncher;
    private final Job bedCacheJob;

    @Scheduled(cron = EVERY_10_MINUTES)
    public void launch() throws Exception {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("launchedAt", System.currentTimeMillis())
                .toJobParameters();
        log.info("병상 정보 갱신 배치 실행");
        jobLauncher.run(bedCacheJob, parameters);
    }
}
