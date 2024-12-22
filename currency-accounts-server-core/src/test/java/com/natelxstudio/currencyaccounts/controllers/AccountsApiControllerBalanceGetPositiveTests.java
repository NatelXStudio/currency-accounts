package com.natelxstudio.currencyaccounts.controllers;

import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.GET_ACCOUNT_CURRENCY_BALANCE;
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
import com.natelxstudio.currencyaccounts.model.GetAccountBalanceResponseDto;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
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
class AccountsApiControllerBalanceGetPositiveTests {
    @LocalServerPort
    private int serverPort;

    @Autowired
    private JpaAccountRepository jpaAccountRepository;

    @Autowired
    private JpaTransactionRepository jpaTransactionRepository;

    @ParameterizedTest
    @MethodSource("testCurrency")
    @SneakyThrows
    void whenUserWantsToGetAccountBalanceForCurrency_thenReturnCorrectResponse(
        Currency currency
    ) {
        UUID accountId = UUID.randomUUID();
        jpaAccountRepository.save(new AccountEntity(
            accountId,
            generateIban(),
            "Jan",
            "Testowy",
            System.currentTimeMillis()));

        BigDecimal balance = BigDecimal.valueOf(123.12d);
        jpaTransactionRepository.save(new TransactionEntity(
            UUID.randomUUID(),
            accountId,
            TransactionEntity.Currency.valueOf(currency.toString()),
            balance,
            System.currentTimeMillis()));

        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .get(GET_ACCOUNT_CURRENCY_BALANCE, accountId.toString(), currency)
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();

        GetAccountBalanceResponseDto responseDto = OBJECT_MAPPER.readValue(response.asString(), GetAccountBalanceResponseDto.class);
        assertThat(responseDto.balance()).isEqualByComparingTo(balance);
    }

    private static Stream<Arguments> testCurrency() {
        return Stream.of(
            Arguments.of(Currency.PLN),
            Arguments.of(Currency.USD));
    }
}