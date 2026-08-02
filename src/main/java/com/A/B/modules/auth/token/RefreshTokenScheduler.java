package com.A.B.modules.auth.token;

import com.A.B.modules.auth.token.store.RdbRefreshTokenStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.refresh-token.store", havingValue = "rdb")
public class RefreshTokenScheduler {

    private final RdbRefreshTokenStore rdbRefreshTokenStore;

    @Scheduled(cron = "${app.refresh-token.purge-cron:0 0 * * * *}")
    public void purgeExpired() {
        int removed = rdbRefreshTokenStore.purgeExpired();
        if (removed > 0) {
            log.info("Purged {} expired refresh tokens", removed);
        }
    }
}
