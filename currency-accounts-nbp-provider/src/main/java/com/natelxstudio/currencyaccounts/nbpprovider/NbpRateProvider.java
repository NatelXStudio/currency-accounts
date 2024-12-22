package com.natelxstudio.currencyaccounts.nbpprovider;

import com.natelxstudio.currencyaccounts.nbpprovider.model.GetRateResponseDto;
import com.natelxstudio.currencyaccounts.ratestore.model.Currency;
import com.natelxstudio.currencyaccounts.ratestore.ports.RateProviderPort;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NbpRateProvider implements RateProviderPort {
    private final NbpRestClient nbpRestClient;

    public record Config(
        String clientBaseUrl,
        long maxAttemptsPerRequest,
        long delayBetweenAttemptsInMillis) {

    }

    public NbpRateProvider(
        Config config
    ) {
        this.nbpRestClient = new NbpRestClient(
            config.clientBaseUrl,
            config.maxAttemptsPerRequest,
            config.delayBetweenAttemptsInMillis);
    }

    @Override
    public BigDecimal getCurrentRate(Currency fromCurrency) {
        GetRateResponseDto getRateResponse = this.nbpRestClient.getExchangeRate(fromCurrency);
        log.info("GetCurrentRate - Got 2xx response from nbp: {}", getRateResponse);
        return getRateResponse.rates().get(0).mid();
    }
}
