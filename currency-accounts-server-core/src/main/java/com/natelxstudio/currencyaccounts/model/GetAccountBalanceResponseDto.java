package com.natelxstudio.currencyaccounts.model;

import java.math.BigDecimal;

public record GetAccountBalanceResponseDto(
    BigDecimal balance
) {
}
