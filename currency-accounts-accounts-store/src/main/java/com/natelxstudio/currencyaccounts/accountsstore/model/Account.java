package com.natelxstudio.currencyaccounts.accountsstore.model;

import java.util.UUID;

public record Account(
    UUID id,
    String iban,
    String firstName,
    String lastName,
    long createdTimestamp
) {
}
