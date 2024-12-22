package com.natelxstudio.currencyaccounts.accountsstore;

import com.natelxstudio.currencyaccounts.accountsstore.model.Account;
import com.natelxstudio.currencyaccounts.accountsstore.model.Currency;
import com.natelxstudio.currencyaccounts.accountsstore.ports.AccountsHolderPort;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class GetBalanceService {
    private final AccountsHolderPort accountsHolderPort;

    BigDecimal getBalance(UUID id, Currency currency) {
        Account account = accountsHolderPort.getAccount(id);
        return accountsHolderPort.getBalance(account.id(), currency);
    }
}
