package com.natelxstudio.currencyaccounts.jparepository.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import com.natelxstudio.currencyaccounts.accountsstore.model.Currency;
import com.natelxstudio.currencyaccounts.accountsstore.model.Transaction;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TransactionEntityMapperTest {
    @ParameterizedTest
    @MethodSource("toEntityMapperTestObject")
    void toDomainObjectMapperTest(Currency currency) {
        UUID accountId = UUID.randomUUID();
        Transaction transaction = new Transaction(
            UUID.randomUUID(),
            currency,
            BigDecimal.valueOf(123.123d),
            System.currentTimeMillis());
        assertThat(TransactionEntityMapper.toEntity(transaction, accountId))
            .satisfies(it -> {
                assertThat(it.getId()).isEqualTo(transaction.id());
                assertThat(it.getAccountId()).isEqualTo(accountId);
                assertThat(it.getCurrency()).hasToString(transaction.currency().toString());
                assertThat(it.getAmount()).isEqualByComparingTo(transaction.amount());
                assertThat(it.getCreatedTimestamp()).isEqualTo(transaction.createdTimestamp());
            });
    }

    private static Stream<Arguments> toEntityMapperTestObject() {
        return Stream.of(
            Arguments.of(Currency.PLN),
            Arguments.of(Currency.USD));
    }
}