package com.natelxstudio.currencyaccounts.nbpprovider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class NbpUtils {
    public static final String GET_RATE_URI = "/api/exchangerates/rates/a/{currencyFrom}";
}
