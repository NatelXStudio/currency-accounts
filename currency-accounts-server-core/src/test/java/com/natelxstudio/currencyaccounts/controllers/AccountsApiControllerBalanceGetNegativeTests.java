package com.natelxstudio.currencyaccounts.controllers;

import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.GET_ACCOUNT_CURRENCY_BALANCE;
import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.OBJECT_MAPPER;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.natelxstudio.currencyaccounts.IntegrationTest;
import com.natelxstudio.currencyaccounts.accountsstore.model.Currency;
import com.natelxstudio.currencyaccounts.model.ApiError;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@IntegrationTest
class AccountsApiControllerBalanceGetNegativeTests {
    private final static String TEST_ID = UUID.randomUUID().toString();
    private final static String TEST_CURRENCY = Currency.USD.toString();

    @LocalServerPort
    private int serverPort;

    @Test
    @SneakyThrows
    void whenUserWantsToGetAccountBalanceForCurrencyAndIdIsNotCorrect_thenReturnBadRequestResponse() {
        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .get(GET_ACCOUNT_CURRENCY_BALANCE, "test", TEST_CURRENCY)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .response();

        ApiError apiError = OBJECT_MAPPER.readValue(response.asString(), ApiError.class);
        assertThat(apiError.code()).isEqualTo(Integer.toString(HttpStatus.BAD_REQUEST.value()));
        assertThat(apiError.message())
            .isNotBlank()
            .contains("Id should has correct UUID format.");
    }

    @Test
    @SneakyThrows
    void whenUserWantsToGetAccountBalanceForCurrencyAndCurrencyIsNotCorrect_thenReturnBadRequestResponse() {
        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .get(GET_ACCOUNT_CURRENCY_BALANCE, TEST_ID, "test")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .response();

        ApiError apiError = OBJECT_MAPPER.readValue(response.asString(), ApiError.class);
        assertThat(apiError.code()).isEqualTo(Integer.toString(HttpStatus.BAD_REQUEST.value()));
        assertThat(apiError.message())
            .isNotBlank()
            .contains("Invalid currency name.");
    }

    @Test
    @SneakyThrows
    void whenUserWantsToGetAccountBalanceForCurrencyAndAccountNotExist_thenReturnNotFoundResponse() {
        String accountId = UUID.randomUUID().toString();

        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .get(GET_ACCOUNT_CURRENCY_BALANCE, accountId, TEST_CURRENCY)
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
}