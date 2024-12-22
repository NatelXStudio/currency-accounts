package com.natelxstudio.currencyaccounts.accountsstore.ports;

import com.natelxstudio.currencyaccounts.accountsstore.model.Account;
import com.natelxstudio.currencyaccounts.accountsstore.model.Currency;
import com.natelxstudio.currencyaccounts.accountsstore.model.Transaction;
import java.math.BigDecimal;
import java.util.UUID;

public interface AccountsHolderPort {
    Account getAccount(UUID id);

    boolean checkIfAccountExistByIban(String iban);

    void insertAccount(Account account);

    void addTransactionToAccount(Transaction transaction, UUID accountId);

    BigDecimal getBalance(UUID id, Currency currency);
}
