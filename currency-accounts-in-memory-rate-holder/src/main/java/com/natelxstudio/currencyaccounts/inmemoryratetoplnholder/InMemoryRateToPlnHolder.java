package com.natelxstudio.currencyaccounts.inmemoryratetoplnholder;

import com.natelxstudio.currencyaccounts.ratestore.model.Currency;
import com.natelxstudio.currencyaccounts.ratestore.ports.RateToPlnHolderPort;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRateToPlnHolder implements RateToPlnHolderPort {
    private final Map<Currency, BigDecimal> ratesMap = new ConcurrentHashMap<>();

    public InMemoryRateToPlnHolder() {
        ratesMap.put(Currency.PLN, BigDecimal.ONE);
    }

    public void addToPlnRate(Currency currency, BigDecimal rate) {
        if(currency.equals(Currency.PLN)) {
            return;
        }
        ratesMap.put(currency, rate);
    }

    @Override
    public BigDecimal getRateToPln(Currency fromCurrency) {
        return ratesMap.get(fromCurrency);
    }
}
