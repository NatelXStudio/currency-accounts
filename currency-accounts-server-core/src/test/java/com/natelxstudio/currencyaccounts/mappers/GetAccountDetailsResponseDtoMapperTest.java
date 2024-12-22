package com.natelxstudio.currencyaccounts.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import com.natelxstudio.currencyaccounts.accountsstore.model.Account;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GetAccountDetailsResponseDtoMapperTest {
    @Test
    void toDtoMapperTest() {
        Account account = new Account(
            UUID.randomUUID(),
            "TEST_IBAN",
            "Jan",
            "Testowy",
            System.currentTimeMillis());
        assertThat(GetAccountDetailsResponseDtoMapper.toDto(account))
            .satisfies(it -> {
                assertThat(account.id()).hasToString(it.id());
                assertThat(account.iban()).isEqualTo(it.iban());
                assertThat(account.firstName()).isEqualTo(it.firstName());
                assertThat(account.lastName()).isEqualTo(it.lastName());
                assertThat(account.createdTimestamp()).isEqualTo(it.createdTimestamp());
            });
    }
}