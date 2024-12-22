package com.natelxstudio.currencyaccounts.controllers;

import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.ADD_NEW_ACCOUNT;
import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.OBJECT_MAPPER;
import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.generateIban;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.natelxstudio.currencyaccounts.IntegrationTest;
import com.natelxstudio.currencyaccounts.jparepository.entities.TransactionEntity;
import com.natelxstudio.currencyaccounts.jparepository.repositories.JpaAccountRepository;
import com.natelxstudio.currencyaccounts.jparepository.repositories.JpaTransactionRepository;
import com.natelxstudio.currencyaccounts.model.InsertBankAccountRequestDto;
import com.natelxstudio.currencyaccounts.model.InsertBankAccountResponseDto;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@IntegrationTest
class AccountsApiControllerPostPositiveTests {
    @LocalServerPort
    private int serverPort;

    @Autowired
    private JpaAccountRepository jpaAccountRepository;

    @Autowired
    private JpaTransactionRepository jpaTransactionRepository;

    @Test
    @SneakyThrows
    void whenUserWantsToAddNewAccount_thenReturnCorrectResponse() {
        InsertBankAccountRequestDto request = new InsertBankAccountRequestDto(
            generateIban(),
            "Jan",
            "Testowy",
            BigDecimal.valueOf(100.12d));

        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .body(OBJECT_MAPPER.writeValueAsString(request))
            .post(ADD_NEW_ACCOUNT)
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();

        InsertBankAccountResponseDto responseDto = OBJECT_MAPPER.readValue(response.asString(), InsertBankAccountResponseDto.class);
        assertThat(jpaAccountRepository.findById(UUID.fromString(responseDto.accountId())))
            .isPresent()
            .hasValueSatisfying(it -> {
                assertThat(it.getId()).hasToString(responseDto.accountId());
                assertThat(it.getIban()).isEqualTo(request.iban());
                assertThat(it.getFirstName()).isEqualTo(request.firstName());
                assertThat(it.getLastName()).isEqualTo(request.lastName());
                assertThat(it.getCreatedTimestamp()).isPositive();
            });
        assertThat(jpaTransactionRepository.sumAmountByAccountIdAndCurrency(
            UUID.fromString(responseDto.accountId()),
            TransactionEntity.Currency.PLN)).isEqualByComparingTo(request.initialBalance());
    }
}