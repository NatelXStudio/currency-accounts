package com.natelxstudio.currencyaccounts.controllers;

import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.EXCHANGE;
import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.OBJECT_MAPPER;
import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.generateIban;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.natelxstudio.currencyaccounts.IntegrationTest;
import com.natelxstudio.currencyaccounts.accountsstore.model.Currency;
import com.natelxstudio.currencyaccounts.jparepository.entities.AccountEntity;
import com.natelxstudio.currencyaccounts.jparepository.entities.TransactionEntity;
import com.natelxstudio.currencyaccounts.jparepository.repositories.JpaAccountRepository;
import com.natelxstudio.currencyaccounts.jparepository.repositories.JpaTransactionRepository;
import com.natelxstudio.currencyaccounts.model.ExchangeRequestDto;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@IntegrationTest
class ExchangeApiControllerPostPositiveTests {
    @LocalServerPort
    private int serverPort;

    @Autowired
    private JpaAccountRepository jpaAccountRepository;

    @Autowired
    private JpaTransactionRepository jpaTransactionRepository;

    @ParameterizedTest
    @MethodSource("testCurrencies")
    @SneakyThrows
    void whenUserWantsToExchangeFromPlnToUsd_thenReturnCorrectResponse(
        Currency fromCurrency,
        Currency toCurrency
    ) {
        UUID accountId = UUID.randomUUID();
        jpaAccountRepository.save(new AccountEntity(
            accountId,
            generateIban(),
            "Jan",
            "Testowy",
            System.currentTimeMillis()));

        BigDecimal initialBalance = BigDecimal.valueOf(100d);
        jpaTransactionRepository.save(new TransactionEntity(
            UUID.randomUUID(),
            accountId,
            TransactionEntity.Currency.valueOf(fromCurrency.toString()),
            initialBalance,
            System.currentTimeMillis()));

        BigDecimal amountToExchange = BigDecimal.valueOf(12d);
        given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .body(OBJECT_MAPPER.writeValueAsString(new ExchangeRequestDto(
                accountId.toString(),
                fromCurrency.toString(),
                toCurrency.toString(),
                amountToExchange)))
            .post(EXCHANGE)
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(jpaTransactionRepository.sumAmountByAccountIdAndCurrency(
            accountId,
            TransactionEntity.Currency.valueOf(fromCurrency.toString())))
            .isLessThan(initialBalance);
        assertThat(jpaTransactionRepository.sumAmountByAccountIdAndCurrency(
            accountId,
            TransactionEntity.Currency.valueOf(toCurrency.toString())))
            .isEqualByComparingTo(amountToExchange);
    }

    private static Stream<Arguments> testCurrencies() {
        return Stream.of(
            Arguments.of(Currency.PLN, Currency.USD),
            Arguments.of(Currency.USD, Currency.PLN));
    }
}