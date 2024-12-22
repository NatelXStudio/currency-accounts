package com.natelxstudio.currencyaccounts.ratestore;

import com.natelxstudio.currencyaccounts.ratestore.model.Currency;
import com.natelxstudio.currencyaccounts.ratestore.ports.RateToPlnHolderPort;
import com.natelxstudio.currencyaccounts.ratestore.ports.RateProviderPort;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class RefreshRatesService {
    private final RateProviderPort rateProviderPort;
    private final RateToPlnHolderPort rateHolderPort;

    void refreshRates() {
        Arrays.stream(Currency.values())
            .filter(it -> !it.equals(Currency.PLN))
            .forEach(it -> rateHolderPort.addToPlnRate(it, rateProviderPort.getCurrentRate(it)));
    }
}
