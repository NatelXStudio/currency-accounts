package com.natelxstudio.currencyaccounts.model;

import com.natelxstudio.currencyaccounts.accountsstore.model.Currency;
import com.natelxstudio.currencyaccounts.validation.ValueOfEnum;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import org.hibernate.validator.constraints.UUID;

public record ExchangeRequestDto(
    @NotNull(message = "Id cannot be null.") @UUID(message = "Id should has correct UUID format.") String id,
    @NotNull(message = "From currency name cannot be null.") @ValueOfEnum(enumClass = Currency.class, message = "Invalid from currency name.") String fromCurrency,
    @NotNull(message = "To currency name cannot be null.") @ValueOfEnum(enumClass = Currency.class, message = "Invalid to currency name.") String toCurrency,
    @NotNull(message = "Amount cannot be null.")
    @Positive(message = "Amount cannot be zero or negative.")
    @Digits(integer = 10, fraction = 2, message = "Invalid amount.") BigDecimal amount) {
}
