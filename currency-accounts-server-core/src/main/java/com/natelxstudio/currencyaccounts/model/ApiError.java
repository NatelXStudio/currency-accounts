package com.natelxstudio.currencyaccounts.model;

public record ApiError(
    String code,
    String message
) {
}
