package com.natelxstudio.currencyaccounts.ratestore.ports;

import com.natelxstudio.currencyaccounts.ratestore.model.Currency;
import java.math.BigDecimal;

public interface RateProviderPort {
    BigDecimal getCurrentRate(Currency fromCurrency);
}
