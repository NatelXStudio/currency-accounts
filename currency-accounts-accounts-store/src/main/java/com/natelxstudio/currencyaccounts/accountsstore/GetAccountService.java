package com.natelxstudio.currencyaccounts.accountsstore;

import com.natelxstudio.currencyaccounts.accountsstore.model.Account;
import com.natelxstudio.currencyaccounts.accountsstore.ports.AccountsHolderPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class GetAccountService {
    private final AccountsHolderPort accountsHolderPort;

    Account getAccount(UUID iban) {
        return accountsHolderPort.getAccount(iban);
    }
}
