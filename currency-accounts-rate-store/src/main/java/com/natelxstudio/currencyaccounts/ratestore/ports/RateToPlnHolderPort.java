package com.natelxstudio.currencyaccounts.ratestore.ports;

import com.natelxstudio.currencyaccounts.ratestore.model.Currency;
import java.math.BigDecimal;

public interface RateToPlnHolderPort {
    void addToPlnRate(Currency currency, BigDecimal rate);

    BigDecimal getRateToPln(Currency fromCurrency);
}
