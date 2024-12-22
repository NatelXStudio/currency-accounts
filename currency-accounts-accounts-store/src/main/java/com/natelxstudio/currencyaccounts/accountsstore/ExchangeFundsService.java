package com.natelxstudio.currencyaccounts.accountsstore;

import com.natelxstudio.currencyaccounts.accountsstore.exceptions.InsufficientFundsException;
import com.natelxstudio.currencyaccounts.accountsstore.model.Account;
import com.natelxstudio.currencyaccounts.accountsstore.model.Currency;
import com.natelxstudio.currencyaccounts.accountsstore.model.Transaction;
import com.natelxstudio.currencyaccounts.accountsstore.ports.AccountsHolderPort;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ExchangeFundsService {
    private final AccountsHolderPort accountsHolderPort;

    void exchangeFunds(
        UUID id,
        Currency fromCurrency,
        Currency toCurrency,
        BigDecimal amount,
        BiFunction<Currency, Currency, BigDecimal> rateProvider
    ) {
        Account account = accountsHolderPort.getAccount(id);
        BigDecimal amountToWithdraw = rateProvider.apply(toCurrency, fromCurrency).multiply(amount);
        BigDecimal currentBalanceFromCurrency = accountsHolderPort.getBalance(id, fromCurrency);

        if (amountToWithdraw.compareTo(currentBalanceFromCurrency) <= 0) {
            insertExchangeRateTransactions(account.id(), fromCurrency, toCurrency, amountToWithdraw, amount);
        } else {
            throw new InsufficientFundsException("Insufficient funds to complete transaction");
        }
    }

    private void insertExchangeRateTransactions(
        UUID accountId,
        Currency fromCurrency,
        Currency toCurrency,
        BigDecimal amountToWithdraw,
        BigDecimal amountToDeposit
    ) {
        long transactionTimestamp = System.currentTimeMillis();
        amountToWithdraw = amountToWithdraw.negate();
        accountsHolderPort.addTransactionToAccount(
            new Transaction(UUID.randomUUID(), fromCurrency, amountToWithdraw, transactionTimestamp),
            accountId);
        accountsHolderPort.addTransactionToAccount(
            new Transaction(UUID.randomUUID(), toCurrency, amountToDeposit, transactionTimestamp),
            accountId);
    }
}
