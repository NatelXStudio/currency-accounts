package com.natelxstudio.currencyaccounts.model;

public record GetAccountDetailsResponseDto(
    String id,
    String iban,
    String firstName,
    String lastName,
    long createdTimestamp
) {
}
