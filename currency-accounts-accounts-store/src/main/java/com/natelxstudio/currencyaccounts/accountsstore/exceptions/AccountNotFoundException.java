package com.natelxstudio.currencyaccounts.accountsstore.exceptions;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
