package com.natelxstudio.currencyaccounts.ratestore;

import com.natelxstudio.currencyaccounts.ratestore.model.Currency;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RateStoreFacade {
    private final RefreshRatesService refreshRatesService;
    private final GetRateService getRateService;

    @PostConstruct
    public void init() {
        refreshRates();
    }

    public void refreshRates() {
        refreshRatesService.refreshRates();
    }

    public BigDecimal getRate(Currency fromCurrency, Currency toCurrency) {
        return getRateService.getRate(fromCurrency, toCurrency);
    }
}
