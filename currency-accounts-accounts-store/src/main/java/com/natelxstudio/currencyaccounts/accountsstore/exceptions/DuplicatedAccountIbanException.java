package com.natelxstudio.currencyaccounts.accountsstore.exceptions;

public class DuplicatedAccountIbanException extends RuntimeException {
    public DuplicatedAccountIbanException(String message) {
        super(message);
    }
}
