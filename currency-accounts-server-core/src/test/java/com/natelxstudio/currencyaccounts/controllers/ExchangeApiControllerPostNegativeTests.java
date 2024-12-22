package com.natelxstudio.currencyaccounts.controllers;

import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.EXCHANGE;
import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.OBJECT_MAPPER;
import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.RANDOM_STRING_UTILS;
import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.generateIban;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.natelxstudio.currencyaccounts.IntegrationTest;
import com.natelxstudio.currencyaccounts.accountsstore.model.Currency;
import com.natelxstudio.currencyaccounts.jparepository.entities.AccountEntity;
import com.natelxstudio.currencyaccounts.jparepository.entities.TransactionEntity;
import com.natelxstudio.currencyaccounts.jparepository.repositories.JpaAccountRepository;
import com.natelxstudio.currencyaccounts.jparepository.repositories.JpaTransactionRepository;
import com.natelxstudio.currencyaccounts.model.ApiError;
import com.natelxstudio.currencyaccounts.model.ExchangeRequestDto;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@IntegrationTest
class ExchangeApiControllerPostNegativeTests {
    private final static ExchangeRequestDto TEST_PAYLOAD = new ExchangeRequestDto(
        UUID.randomUUID().toString(),
        Currency.PLN.toString(),
        Currency.USD.toString(),
        BigDecimal.valueOf(123.12d));

    @LocalServerPort
    private int serverPort;

    @Autowired
    private JpaAccountRepository jpaAccountRepository;

    @Autowired
    private JpaTransactionRepository jpaTransactionRepository;

    @ParameterizedTest
    @MethodSource("notCorrectId")
    @SneakyThrows
    void whenUserWantsToExchangeAndIdIsNotCorrect_thenReturnBadRequestResponse(
        String id,
        String expectedMessage
    ) {
        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .body(OBJECT_MAPPER.writeValueAsString(new ExchangeRequestDto(
                id,
                TEST_PAYLOAD.fromCurrency(),
                TEST_PAYLOAD.toCurrency(),
                TEST_PAYLOAD.amount())))
            .post(EXCHANGE)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .response();

        ApiError apiError = OBJECT_MAPPER.readValue(response.asString(), ApiError.class);
        assertThat(apiError.code()).isEqualTo(Integer.toString(HttpStatus.BAD_REQUEST.value()));
        assertThat(apiError.message())
            .isNotBlank()
            .contains(expectedMessage);
    }

    private static Stream<Arguments> notCorrectId() {
        return Stream.of(
            Arguments.of(null, "Id cannot be null."),
            Arguments.of("", "Id should has correct UUID format."),
            Arguments.of(RANDOM_STRING_UTILS.next(10, false, true), "Id should has correct UUID format."));
    }

    @ParameterizedTest
    @MethodSource("notCorrectFromCurrency")
    @SneakyThrows
    void whenUserWantsToExchangeAndFromCurrencyIsNotCorrect_thenReturnBadRequestResponse(
        String fromCurrency,
        String expectedMessage
    ) {
        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .body(OBJECT_MAPPER.writeValueAsString(new ExchangeRequestDto(
                TEST_PAYLOAD.id(),
                fromCurrency,
                TEST_PAYLOAD.toCurrency(),
                TEST_PAYLOAD.amount())))
            .post(EXCHANGE)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .response();

        ApiError apiError = OBJECT_MAPPER.readValue(response.asString(), ApiError.class);
        assertThat(apiError.code()).isEqualTo(Integer.toString(HttpStatus.BAD_REQUEST.value()));
        assertThat(apiError.message())
            .isNotBlank()
            .contains(expectedMessage);
    }

    private static Stream<Arguments> notCorrectFromCurrency() {
        return Stream.of(
            Arguments.of(null, "From currency name cannot be null."),
            Arguments.of("", "Invalid from currency name."),
            Arguments.of("test", "Invalid from currency name."));
    }

    @ParameterizedTest
    @MethodSource("notCorrectToCurrency")
    @SneakyThrows
    void whenUserWantsToExchangeAndToCurrencyIsNotCorrect_thenReturnBadRequestResponse(
        String toCurrency,
        String expectedMessage
    ) {
        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .body(OBJECT_MAPPER.writeValueAsString(new ExchangeRequestDto(
                TEST_PAYLOAD.id(),
                TEST_PAYLOAD.fromCurrency(),
                toCurrency,
                TEST_PAYLOAD.amount())))
            .post(EXCHANGE)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .response();

        ApiError apiError = OBJECT_MAPPER.readValue(response.asString(), ApiError.class);
        assertThat(apiError.code()).isEqualTo(Integer.toString(HttpStatus.BAD_REQUEST.value()));
        assertThat(apiError.message())
            .isNotBlank()
            .contains(expectedMessage);
    }

    private static Stream<Arguments> notCorrectToCurrency() {
        return Stream.of(
            Arguments.of(null, "To currency name cannot be null."),
            Arguments.of("", "Invalid to currency name."),
            Arguments.of("test", "Invalid to currency name."));
    }

    @ParameterizedTest
    @MethodSource("notCorrectAmount")
    @SneakyThrows
    void whenUserWantsToExchangeAndAmountIsNotCorrect_thenReturnBadRequestResponse(
        BigDecimal amount,
        String expectedMessage
    ) {
        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .body(OBJECT_MAPPER.writeValueAsString(new ExchangeRequestDto(
                TEST_PAYLOAD.id(),
                TEST_PAYLOAD.fromCurrency(),
                TEST_PAYLOAD.toCurrency(),
                amount)))
            .post(EXCHANGE)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .response();

        ApiError apiError = OBJECT_MAPPER.readValue(response.asString(), ApiError.class);
        assertThat(apiError.code()).isEqualTo(Integer.toString(HttpStatus.BAD_REQUEST.value()));
        assertThat(apiError.message())
            .isNotBlank()
            .contains(expectedMessage);
    }

    private static Stream<Arguments> notCorrectAmount() {
        return Stream.of(
            Arguments.of(null, "Amount cannot be null."),
            Arguments.of(BigDecimal.ZERO, "Amount cannot be zero or negative."),
            Arguments.of(BigDecimal.valueOf(-123d), "Amount cannot be zero or negative."),
            Arguments.of(BigDecimal.valueOf(123.123d), "Invalid amount."),
            Arguments.of(new BigDecimal("11111111111.11"), "Invalid amount."));
    }

    @Test
    @SneakyThrows
    void whenUserWantsToExchangeAndAccountNotExist_thenReturnNotFoundResponse() {
        String accountId = UUID.randomUUID().toString();

        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .body(OBJECT_MAPPER.writeValueAsString(new ExchangeRequestDto(
                accountId,
                TEST_PAYLOAD.fromCurrency(),
                TEST_PAYLOAD.toCurrency(),
                TEST_PAYLOAD.amount())))
            .post(EXCHANGE)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .extract()
            .response();

        ApiError apiError = OBJECT_MAPPER.readValue(response.asString(), ApiError.class);
        assertThat(apiError.code()).isEqualTo(Integer.toString(HttpStatus.NOT_FOUND.value()));
        assertThat(apiError.message())
            .isNotBlank()
            .contains(accountId);
    }

    @Test
    @SneakyThrows
    void whenUserWantsToExchangeAndAccountHasInsufficientFunds_thenReturnForbiddenResponse() {
        UUID accountId = UUID.randomUUID();
        jpaAccountRepository.save(new AccountEntity(
            accountId,
            generateIban(),
            "Jan",
            "Testowy",
            System.currentTimeMillis()));

        jpaTransactionRepository.save(new TransactionEntity(
            UUID.randomUUID(),
            accountId,
            TransactionEntity.Currency.PLN,
            BigDecimal.ONE,
            System.currentTimeMillis()));

        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .body(OBJECT_MAPPER.writeValueAsString(new ExchangeRequestDto(
                accountId.toString(),
                TEST_PAYLOAD.fromCurrency(),
                TEST_PAYLOAD.toCurrency(),
                TEST_PAYLOAD.amount())))
            .post(EXCHANGE)
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .extract()
            .response();

        ApiError apiError = OBJECT_MAPPER.readValue(response.asString(), ApiError.class);
        assertThat(apiError.code()).isEqualTo(Integer.toString(HttpStatus.FORBIDDEN.value()));
        assertThat(apiError.message())
            .isNotBlank()
            .contains("Insufficient funds to complete transaction");
    }
}