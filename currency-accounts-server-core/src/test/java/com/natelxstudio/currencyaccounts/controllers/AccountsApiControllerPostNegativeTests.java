package com.natelxstudio.currencyaccounts.controllers;

import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.ADD_NEW_ACCOUNT;
import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.OBJECT_MAPPER;
import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.RANDOM_STRING_UTILS;
import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.generateIban;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.natelxstudio.currencyaccounts.IntegrationTest;
import com.natelxstudio.currencyaccounts.jparepository.entities.AccountEntity;
import com.natelxstudio.currencyaccounts.jparepository.repositories.JpaAccountRepository;
import com.natelxstudio.currencyaccounts.model.ApiError;
import com.natelxstudio.currencyaccounts.model.InsertBankAccountRequestDto;
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
class AccountsApiControllerPostNegativeTests {
    private final static InsertBankAccountRequestDto TEST_PAYLOAD = new InsertBankAccountRequestDto(
        generateIban(),
        "Jan",
        "Testowy",
        BigDecimal.valueOf(123.12d));

    @LocalServerPort
    private int serverPort;

    @Autowired
    private JpaAccountRepository jpaAccountRepository;

    @ParameterizedTest
    @MethodSource("notCorrectIban")
    @SneakyThrows
    void whenUserWantsToAddNewAccountAndIbanIsNotCorrect_thenReturnBadRequestResponse(
        String iban,
        String expectedMessage
    ) {
        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .body(OBJECT_MAPPER.writeValueAsString(new InsertBankAccountRequestDto(
                iban,
                TEST_PAYLOAD.firstName(),
                TEST_PAYLOAD.lastName(),
                TEST_PAYLOAD.initialBalance())))
            .post(ADD_NEW_ACCOUNT)
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

    private static Stream<Arguments> notCorrectIban() {
        return Stream.of(
            Arguments.of(null, "Iban cannot be null or empty."),
            Arguments.of("", "Iban cannot be null or empty."),
            Arguments.of(RANDOM_STRING_UTILS.next(12, false, true), "Iban should has correct IBAN format."));
    }

    @ParameterizedTest
    @MethodSource("notCorrectFirstName")
    @SneakyThrows
    void whenUserWantsToAddNewAccountAndFirstNameIsNotCorrect_thenReturnBadRequestResponse(
        String firstName,
        String expectedMessage
    ) {
        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .body(OBJECT_MAPPER.writeValueAsString(new InsertBankAccountRequestDto(
                TEST_PAYLOAD.iban(),
                firstName,
                TEST_PAYLOAD.lastName(),
                TEST_PAYLOAD.initialBalance())))
            .post(ADD_NEW_ACCOUNT)
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

    private static Stream<Arguments> notCorrectFirstName() {
        return Stream.of(
            Arguments.of(null, "First name cannot be null."),
            Arguments.of("", "Invalid first name."),
            Arguments.of(RANDOM_STRING_UTILS.next(20, true, true), "Invalid first name."),
            Arguments.of(RANDOM_STRING_UTILS.next(31, true, false), "Invalid first name."));
    }

    @ParameterizedTest
    @MethodSource("notCorrectLastName")
    @SneakyThrows
    void whenUserWantsToAddNewAccountAndLastNameIsNotCorrect_thenReturnBadRequestResponse(
        String lastName,
        String expectedMessage
    ) {
        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .body(OBJECT_MAPPER.writeValueAsString(new InsertBankAccountRequestDto(
                TEST_PAYLOAD.iban(),
                TEST_PAYLOAD.firstName(),
                lastName,
                TEST_PAYLOAD.initialBalance())))
            .post(ADD_NEW_ACCOUNT)
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

    private static Stream<Arguments> notCorrectLastName() {
        return Stream.of(
            Arguments.of(null, "Last name cannot be null."),
            Arguments.of("", "Invalid last name."),
            Arguments.of(RANDOM_STRING_UTILS.next(20, true, true), "Invalid last name."),
            Arguments.of(RANDOM_STRING_UTILS.next(51, true, false), "Invalid last name."));
    }

    @ParameterizedTest
    @MethodSource("notCorrectInitialBalance")
    @SneakyThrows
    void whenUserWantsToAddNewAccountAndInitialBalanceIsNotCorrect_thenReturnBadRequestResponse(
        BigDecimal initialBalance,
        String expectedMessage
    ) {
        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .body(OBJECT_MAPPER.writeValueAsString(new InsertBankAccountRequestDto(
                TEST_PAYLOAD.iban(),
                TEST_PAYLOAD.firstName(),
                TEST_PAYLOAD.lastName(),
                initialBalance)))
            .post(ADD_NEW_ACCOUNT)
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

    private static Stream<Arguments> notCorrectInitialBalance() {
        return Stream.of(
            Arguments.of(null, "Initial balance cannot be null."),
            Arguments.of(BigDecimal.ZERO, "Initial balance cannot be zero or negative."),
            Arguments.of(BigDecimal.valueOf(-123d), "Initial balance cannot be zero or negative."),
            Arguments.of(BigDecimal.valueOf(123.123d), "Invalid initial balance."),
            Arguments.of(new BigDecimal("1111111111111111111111111111111.11"), "Invalid initial balance."));
    }

    @Test
    @SneakyThrows
    void whenUserWantsToAddNewAccountWithAlreadyExistIban_thenReturnBadRequestResponse() {
        String iban = generateIban();

        jpaAccountRepository.save(new AccountEntity(
            UUID.randomUUID(),
            iban,
            "Jan",
            "testowy",
            System.currentTimeMillis()));

        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .body(OBJECT_MAPPER.writeValueAsString(new InsertBankAccountRequestDto(
                iban,
                TEST_PAYLOAD.firstName(),
                TEST_PAYLOAD.lastName(),
                TEST_PAYLOAD.initialBalance())))
            .post(ADD_NEW_ACCOUNT)
            .then()
            .statusCode(HttpStatus.CONFLICT.value())
            .extract()
            .response();

        ApiError apiError = OBJECT_MAPPER.readValue(response.asString(), ApiError.class);
        assertThat(apiError.code()).isEqualTo(Integer.toString(HttpStatus.CONFLICT.value()));
        assertThat(apiError.message())
            .isNotBlank()
            .contains(iban);
    }
}