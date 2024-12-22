package com.natelxstudio.currencyaccounts.accountsstore;

import com.natelxstudio.currencyaccounts.accountsstore.ports.AccountsHolderPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AccountsStoreConfiguration {
    @Bean
    AccountsStoreFacade accountsStoreFacade(
        AccountsHolderPort accountsHolderPort
    ) {
        return new AccountsStoreFacade(
            new InsertAccountService(accountsHolderPort),
            new ExchangeFundsService(accountsHolderPort),
            new GetBalanceService(accountsHolderPort),
            new GetAccountService(accountsHolderPort));
    }
}
