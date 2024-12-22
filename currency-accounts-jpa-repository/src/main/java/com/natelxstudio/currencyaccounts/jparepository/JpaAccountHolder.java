package com.natelxstudio.currencyaccounts.jparepository;

import com.natelxstudio.currencyaccounts.accountsstore.exceptions.AccountNotFoundException;
import com.natelxstudio.currencyaccounts.accountsstore.model.Account;
import com.natelxstudio.currencyaccounts.accountsstore.model.Currency;
import com.natelxstudio.currencyaccounts.accountsstore.model.Transaction;
import com.natelxstudio.currencyaccounts.accountsstore.ports.AccountsHolderPort;
import com.natelxstudio.currencyaccounts.jparepository.entities.TransactionEntity;
import com.natelxstudio.currencyaccounts.jparepository.mappers.AccountEntityMapper;
import com.natelxstudio.currencyaccounts.jparepository.mappers.TransactionEntityMapper;
import com.natelxstudio.currencyaccounts.jparepository.repositories.JpaAccountRepository;
import com.natelxstudio.currencyaccounts.jparepository.repositories.JpaTransactionRepository;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaAccountHolder implements AccountsHolderPort {
    private final JpaAccountRepository jpaAccountRepository;
    private final JpaTransactionRepository jpaTransactionRepository;

    @Override
    public Account getAccount(UUID id) {
        return jpaAccountRepository.findById(id)
            .map(AccountEntityMapper::toDomainObject)
            .orElseThrow(() -> new AccountNotFoundException("Account with id: " + id + "not exist"));
    }

    @Override
    public boolean checkIfAccountExistByIban(String iban) {
        return jpaAccountRepository.existsByIban(iban);
    }

    @Override
    public void insertAccount(Account account) {
        jpaAccountRepository.save(AccountEntityMapper.toEntity(account));
    }

    @Override
    public void addTransactionToAccount(Transaction transaction, UUID accountId) {
        jpaTransactionRepository.save(TransactionEntityMapper.toEntity(transaction, accountId));
    }

    @Override
    public BigDecimal getBalance(UUID id, Currency currency) {
        return jpaTransactionRepository.sumAmountByAccountIdAndCurrency(id, TransactionEntity.Currency.valueOf(currency.toString()));
    }
}
