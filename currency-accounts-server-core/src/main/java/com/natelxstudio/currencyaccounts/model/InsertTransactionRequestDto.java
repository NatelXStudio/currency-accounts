package com.natelxstudio.currencyaccounts.model;

import com.natelxstudio.currencyaccounts.validation.Uuid;
import com.natelxstudio.currencyaccounts.validation.ValueOfEnum;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InsertTransactionRequestDto {
    @Uuid(message = "Transactiond id should has correct UUID format.")
    private final String transactionId;

    @ValueOfEnum(enumClass = Currency.class, message = "Invalid currentFrom field.")
    private final String currencyFrom;

    @ValueOfEnum(enumClass = Currency.class, message = "Invalid currentFrom field.")
    private final String currencyTo;

    private final BigDecimal amount;

    public enum Currency {
        EUR, PLN
    }
}
