package com.natelxstudio.currencyaccounts.accountsstore;

import com.natelxstudio.currencyaccounts.accountsstore.model.Account;
import com.natelxstudio.currencyaccounts.accountsstore.model.Currency;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class AccountsStoreFacade {
    private final InsertAccountService insertAccountService;
    private final ExchangeFundsService exchangeFundsService;
    private final GetBalanceService getBalanceService;
    private final GetAccountService getAccountService;

    @Transactional
    public UUID insertAccount(
        String iban,
        String firstName,
        String lastName,
        Currency initialCurrency,
        BigDecimal initialBalance
    ) {
        return insertAccountService.insertAccount(iban, firstName, lastName, initialCurrency, initialBalance);
    }

    @Transactional
    public void exchangeFunds(
        UUID id,
        Currency fromCurrency,
        Currency toCurrency,
        BigDecimal amount,
        BiFunction<Currency, Currency, BigDecimal> rateProvider
    ) {
        exchangeFundsService.exchangeFunds(id, fromCurrency, toCurrency, amount, rateProvider);
    }

    public BigDecimal getBalance(UUID id, Currency currency) {
        return getBalanceService.getBalance(id, currency);
    }

    public Account getAccount(UUID id) {
        return getAccountService.getAccount(id);
    }
}
