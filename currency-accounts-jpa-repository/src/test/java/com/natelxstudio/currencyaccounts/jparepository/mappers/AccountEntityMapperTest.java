package com.natelxstudio.currencyaccounts.jparepository.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import com.natelxstudio.currencyaccounts.accountsstore.model.Account;
import com.natelxstudio.currencyaccounts.jparepository.entities.AccountEntity;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccountEntityMapperTest {
    @Test
    void toDomainObjectMapperTest() {
        AccountEntity accountEntity = new AccountEntity(
            UUID.randomUUID(),
            "TEST_IBAN",
            "Jan",
            "Testowy",
            System.currentTimeMillis());
        assertThat(AccountEntityMapper.toDomainObject(accountEntity))
            .satisfies(it -> assertAccountEntity(it, accountEntity));
    }

    @Test
    void toEntityMapperTest() {
        Account account = new Account(
            UUID.randomUUID(),
            "TEST_IBAN",
            "Jan",
            "Testowy",
            System.currentTimeMillis());
        assertThat(AccountEntityMapper.toEntity(account))
            .satisfies(it -> assertAccountEntity(account, it));
    }

    private static void assertAccountEntity(
        Account account,
        AccountEntity accountEntity
    ) {
        assertThat(account.id()).isEqualTo(accountEntity.getId());
        assertThat(account.iban()).isEqualTo(accountEntity.getIban());
        assertThat(account.firstName()).isEqualTo(accountEntity.getFirstName());
        assertThat(account.lastName()).isEqualTo(accountEntity.getLastName());
        assertThat(account.createdTimestamp()).isEqualTo(accountEntity.getCreatedTimestamp());
    }
}