package com.natelxstudio.currencyaccounts.accountsstore.model;

import java.math.BigDecimal;
import java.util.UUID;

public record Transaction(
    UUID id,
    Currency currency,
    BigDecimal amount,
    long createdTimestamp
) {
}
