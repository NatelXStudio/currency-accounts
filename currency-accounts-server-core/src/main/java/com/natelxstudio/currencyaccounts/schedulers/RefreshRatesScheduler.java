package com.natelxstudio.currencyaccounts.schedulers;

import com.natelxstudio.currencyaccounts.exceptions.RefreshRatesException;
import com.natelxstudio.currencyaccounts.ratestore.RateStoreFacade;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("useSchedulers")
public class RefreshRatesScheduler {
    private final RateStoreFacade rateStoreFacade;

    @Value("${refresh-rates.scheduler.retry-count}")
    private int retryCount;

    @Value("${refresh-rates.scheduler.retry-delay-in-sec}")
    private int retryDelayInSec;

    @Scheduled(cron = "${refresh-rates.scheduler.cron}")
    public void refreshRates() {
        log.info("Starting refreshing rates scheduler");
        Runnable refreshRatesRunnable = rateStoreFacade::refreshRates;
        try {
            refreshRatesRunnable.run();
            throw new Exception("aaa");
        } catch (Exception e) {
            log.warn("Cannot get NBP rates, start retrying", e);
            retryRefreshRates(refreshRatesRunnable);
        }
        log.info("Refreshing rates scheduler finished");
    }

    public void retryRefreshRates(Runnable runnable) {
        for (int i = 0; i < retryCount; i++) {
            try {
                TimeUnit.SECONDS.sleep(retryDelayInSec);
                runnable.run();
                return;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        throw new RefreshRatesException("Cannot refresh NBP rates");
    }
}
