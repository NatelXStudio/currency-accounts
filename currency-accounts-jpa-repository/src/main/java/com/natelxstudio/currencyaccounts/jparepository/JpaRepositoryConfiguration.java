package com.natelxstudio.currencyaccounts.jparepository;

import com.natelxstudio.currencyaccounts.accountsstore.ports.AccountsHolderPort;
import com.natelxstudio.currencyaccounts.jparepository.repositories.JpaAccountRepository;
import com.natelxstudio.currencyaccounts.jparepository.repositories.JpaTransactionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
class JpaRepositoryConfiguration {
    @Bean
    AccountsHolderPort accountsHolderPort(
        JpaAccountRepository jpaAccountRepository,
        JpaTransactionRepository jpaTransactionRepository
    ) {
        return new JpaAccountHolder(jpaAccountRepository, jpaTransactionRepository);
    }
}
