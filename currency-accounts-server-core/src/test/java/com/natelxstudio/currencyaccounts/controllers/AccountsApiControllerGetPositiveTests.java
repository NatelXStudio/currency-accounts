package com.natelxstudio.currencyaccounts.controllers;

import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.GET_ACCOUNTS_DETAILS;
import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.OBJECT_MAPPER;
import static com.natelxstudio.currencyaccounts.controllers.ApiTestsUtils.generateIban;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.natelxstudio.currencyaccounts.IntegrationTest;
import com.natelxstudio.currencyaccounts.jparepository.entities.AccountEntity;
import com.natelxstudio.currencyaccounts.jparepository.repositories.JpaAccountRepository;
import com.natelxstudio.currencyaccounts.model.GetAccountDetailsResponseDto;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@IntegrationTest
class AccountsApiControllerGetPositiveTests {
    @LocalServerPort
    private int serverPort;

    @Autowired
    private JpaAccountRepository jpaAccountRepository;

    @Test
    @SneakyThrows
    void whenUserWantsToGetAccountDetails_thenReturnCorrectResponse() {
        AccountEntity accountEntity = new AccountEntity(
            UUID.randomUUID(),
            generateIban(),
            "Jan",
            "Testowy",
            System.currentTimeMillis());
        jpaAccountRepository.save(accountEntity);

        Response response = given()
            .port(serverPort)
            .when()
            .contentType(ContentType.JSON)
            .get(GET_ACCOUNTS_DETAILS, accountEntity.getId().toString())
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();

        GetAccountDetailsResponseDto responseDto = OBJECT_MAPPER.readValue(response.asString(), GetAccountDetailsResponseDto.class);
        assertThat(responseDto).satisfies(it -> {
            assertThat(it.id()).isEqualTo(accountEntity.getId().toString());
            assertThat(it.iban()).isEqualTo(accountEntity.getIban());
            assertThat(it.firstName()).isEqualTo(accountEntity.getFirstName());
            assertThat(it.lastName()).isEqualTo(accountEntity.getLastName());
            assertThat(it.createdTimestamp()).isEqualTo(accountEntity.getCreatedTimestamp());
        });
    }
}