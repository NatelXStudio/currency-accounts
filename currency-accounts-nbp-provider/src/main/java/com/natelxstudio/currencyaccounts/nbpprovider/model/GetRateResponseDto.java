package com.natelxstudio.currencyaccounts.nbpprovider.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record GetRateResponseDto(
    String table,
    String currency,
    String code,
    List<Rate> rates
) {
    public record Rate(
        String no,
        LocalDate effectiveDate,
        BigDecimal mid
    ) {
    }
}
