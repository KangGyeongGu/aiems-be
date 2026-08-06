package com.aiems.be.modules.hospital.bed.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.role.batch.enabled", havingValue = "true")
public class BedCacheJobScheduler {

    private static final String EVERY_10_MINUTES = "0 */10 * * * *";

    private final JobLauncher jobLauncher;
    private final Job bedCacheJob;

    @Scheduled(cron = EVERY_10_MINUTES)
    @SchedulerLock(name = "bedCacheJob", lockAtMostFor = "PT9M", lockAtLeastFor = "PT1M")
    public void launch() throws Exception {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("launchedAt", System.currentTimeMillis())
                .toJobParameters();

        log.info("병상 정보 갱신 배치 실행");
        jobLauncher.run(bedCacheJob, parameters);
    }

}
