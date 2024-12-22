package com.natelxstudio.currencyaccounts.accountsstore;

import com.natelxstudio.currencyaccounts.accountsstore.exceptions.DuplicatedAccountIbanException;
import com.natelxstudio.currencyaccounts.accountsstore.model.Account;
import com.natelxstudio.currencyaccounts.accountsstore.model.Currency;
import com.natelxstudio.currencyaccounts.accountsstore.model.Transaction;
import com.natelxstudio.currencyaccounts.accountsstore.ports.AccountsHolderPort;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class InsertAccountService {
    private final AccountsHolderPort accountsHolderPort;

    UUID insertAccount(
        String iban,
        String firstName,
        String lastName,
        Currency initialCurrency,
        BigDecimal initialBalance
    ) {
        if (!accountsHolderPort.checkIfAccountExistByIban(iban)) {
            long createdTimestamp = System.currentTimeMillis();
            Account account = new Account(UUID.randomUUID(), iban, firstName, lastName, createdTimestamp);
            accountsHolderPort.insertAccount(account);
            accountsHolderPort.addTransactionToAccount(
                new Transaction(UUID.randomUUID(), initialCurrency, initialBalance, createdTimestamp),
                account.id());
            return account.id();
        } else {
            throw new DuplicatedAccountIbanException("Account with iban: " + iban + " already exist");
        }
    }
}
