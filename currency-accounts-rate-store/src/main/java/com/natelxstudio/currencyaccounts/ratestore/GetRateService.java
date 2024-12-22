package com.natelxstudio.currencyaccounts.ratestore;

import com.natelxstudio.currencyaccounts.ratestore.model.Currency;
import com.natelxstudio.currencyaccounts.ratestore.ports.RateToPlnHolderPort;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class GetRateService {
    private final RateToPlnHolderPort rateHolderPort;

    BigDecimal getRate(Currency fromCurrency, Currency toCurrency) {
        BigDecimal fromCurrencyToPln = rateHolderPort.getRateToPln(fromCurrency);
        BigDecimal toCurrencyToPln = rateHolderPort.getRateToPln(toCurrency);
        return fromCurrencyToPln.divide(toCurrencyToPln, 4, RoundingMode.HALF_DOWN);
    }
}
