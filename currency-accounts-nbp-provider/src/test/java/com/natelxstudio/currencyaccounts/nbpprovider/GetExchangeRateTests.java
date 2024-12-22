package com.natelxstudio.currencyaccounts.nbpprovider;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.natelxstudio.currencyaccounts.nbpprovider.NbpTestsUtils.BAD_REQUEST_PATH;
import static com.natelxstudio.currencyaccounts.nbpprovider.NbpTestsUtils.FORBIDDEN_PATH;
import static com.natelxstudio.currencyaccounts.nbpprovider.NbpTestsUtils.NOT_FOUND_PATH;
import static com.natelxstudio.currencyaccounts.nbpprovider.NbpTestsUtils.SERVER_ERROR_PATH;
import static com.natelxstudio.currencyaccounts.nbpprovider.NbpTestsUtils.UNAUTHORIZED_PATH;
import static com.natelxstudio.currencyaccounts.nbpprovider.NbpTestsUtils.UNKNOWN_ERROR_PATH;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.natelxstudio.currencyaccounts.nbpprovider.exceptions.UnauthorizedException;
import com.natelxstudio.currencyaccounts.nbpprovider.exceptions.BadRequestException;
import com.natelxstudio.currencyaccounts.nbpprovider.exceptions.ForbiddenException;
import com.natelxstudio.currencyaccounts.nbpprovider.exceptions.NotFoundException;
import com.natelxstudio.currencyaccounts.nbpprovider.exceptions.ServerErrorException;
import com.natelxstudio.currencyaccounts.nbpprovider.exceptions.UnknownServerException;
import com.natelxstudio.currencyaccounts.ratestore.model.Currency;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpHeaders;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetExchangeRateTests {
    private static final int MOCK_SERVER_PORT = 9091;
    private static final String GET_EXCHANGE_TEST_ENDPOINT = "/getExchangeRate";
    private static final Currency TEST_CURRENCY_TO = Currency.USD;
    private final WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(MOCK_SERVER_PORT));

    @BeforeAll
    public void setUp() {
        wireMockServer.start();
        if (!wireMockServer.isRunning()) {
            throw new RuntimeException("Cannot start wire mock server");
        }

        wireMockServer.stubFor(get(urlPathTemplate(GET_EXCHANGE_TEST_ENDPOINT + NbpUtils.GET_RATE_URI))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                .withBody(NbpTestsUtils.GET_RATE_OK_BODY)));
        wireMockServer.stubFor(get(urlPathTemplate(BAD_REQUEST_PATH + GET_EXCHANGE_TEST_ENDPOINT + NbpUtils.GET_RATE_URI))
            .willReturn(aResponse()
                .withStatus(400)
                .withBody(NbpTestsUtils.BAD_REQUEST_RESPONSE_BODY)));
        wireMockServer.stubFor(get(urlPathTemplate(UNAUTHORIZED_PATH + GET_EXCHANGE_TEST_ENDPOINT + NbpUtils.GET_RATE_URI))
            .willReturn(aResponse()
                .withStatus(401)
                .withBody(NbpTestsUtils.UNAUTHORIZED_RESPONSE_BODY)));
        wireMockServer.stubFor(get(urlPathTemplate(FORBIDDEN_PATH + GET_EXCHANGE_TEST_ENDPOINT + NbpUtils.GET_RATE_URI))
            .willReturn(aResponse()
                .withStatus(403)
                .withBody(NbpTestsUtils.FORBIDDEN_RESPONSE_BODY)));
        wireMockServer.stubFor(get(urlPathTemplate(NOT_FOUND_PATH + GET_EXCHANGE_TEST_ENDPOINT + NbpUtils.GET_RATE_URI))
            .willReturn(aResponse()
                .withStatus(404)
                .withBody(NbpTestsUtils.NOT_FOUND_RESPONSE_BODY)));
        wireMockServer.stubFor(get(urlPathTemplate(UNKNOWN_ERROR_PATH + GET_EXCHANGE_TEST_ENDPOINT + NbpUtils.GET_RATE_URI))
            .willReturn(aResponse()
                .withStatus(410)
                .withBody(NbpTestsUtils.UNKNOWN_RESPONSE_BODY)));
        wireMockServer.stubFor(get(urlPathTemplate(SERVER_ERROR_PATH + GET_EXCHANGE_TEST_ENDPOINT + NbpUtils.GET_RATE_URI))
            .willReturn(aResponse().withStatus(500)));
    }

    @AfterAll
    public void cleanUp() {
        wireMockServer.stop();
        if (wireMockServer.isRunning()) {
            throw new RuntimeException("Cannot stop wire mock server");
        }
    }

    @Test
    void whenClientWantsToGetExchangeRate_thenReturnCorrectResponse() {
        NbpRateProvider rateProvider = new NbpRateProvider(new NbpRateProvider.Config(
            "http://localhost:" + MOCK_SERVER_PORT + GET_EXCHANGE_TEST_ENDPOINT,
            NbpTestsUtils.MAX_ATTEMPTS_PER_REQUEST,
            1));
        assertThatCode(() -> rateProvider.getCurrentRate(TEST_CURRENCY_TO))
            .doesNotThrowAnyException();
    }

    @Test
    void whenClientWantsToGetExchangeRateWithNotCorrectBody_thenThrowBadRequestException() {
        NbpRateProvider badRequestRateProvider = new NbpRateProvider(new NbpRateProvider.Config(
            "http://localhost:" + MOCK_SERVER_PORT + BAD_REQUEST_PATH + GET_EXCHANGE_TEST_ENDPOINT,
            NbpTestsUtils.MAX_ATTEMPTS_PER_REQUEST,
            1));
        assertThatThrownBy(() -> badRequestRateProvider.getCurrentRate(TEST_CURRENCY_TO))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("BAD REQUEST");
    }

    @Test
    void whenClientWantsToGetExchangeRateWithoutCredentials_thenThrowUnauthorizedException() {
        NbpRateProvider unauthorizedRateProvider = new NbpRateProvider(new NbpRateProvider.Config(
            "http://localhost:" + MOCK_SERVER_PORT + UNAUTHORIZED_PATH + GET_EXCHANGE_TEST_ENDPOINT,
            NbpTestsUtils.MAX_ATTEMPTS_PER_REQUEST,
            1));
        assertThatThrownBy(() -> unauthorizedRateProvider.getCurrentRate(TEST_CURRENCY_TO))
            .isInstanceOf(UnauthorizedException.class)
            .hasMessageContaining("UNAUTHORIZED");
    }

    @Test
    void whenClientWantsToGetExchangeRateWithNotCorrectRoles_thenThrowForbiddenException() {
        NbpRateProvider forbiddenRateProvider = new NbpRateProvider(new NbpRateProvider.Config(
            "http://localhost:" + MOCK_SERVER_PORT + FORBIDDEN_PATH + GET_EXCHANGE_TEST_ENDPOINT,
            NbpTestsUtils.MAX_ATTEMPTS_PER_REQUEST,
            1));
        assertThatThrownBy(() -> forbiddenRateProvider.getCurrentRate(TEST_CURRENCY_TO))
            .isInstanceOf(ForbiddenException.class)
            .hasMessageContaining("FORBIDDEN");
    }

    @Test
    void whenClientWantsToGetExchangeRateWithNotCorrectCurrencyTo_thenThrowNotFoundException() {
        NbpRateProvider notFoundRateProvider = new NbpRateProvider(new NbpRateProvider.Config(
            "http://localhost:" + MOCK_SERVER_PORT + NOT_FOUND_PATH + GET_EXCHANGE_TEST_ENDPOINT,
            NbpTestsUtils.MAX_ATTEMPTS_PER_REQUEST,
            1));
        assertThatThrownBy(() -> notFoundRateProvider.getCurrentRate(TEST_CURRENCY_TO))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("NOT FOUND");
    }

    @Test
    void whenClientWantsToGetExchangeRateAndServerRespondWithGone_thenThrowUnknownException() {
        NbpRateProvider unknownRateProvider = new NbpRateProvider(new NbpRateProvider.Config(
            "http://localhost:" + MOCK_SERVER_PORT + UNKNOWN_ERROR_PATH + GET_EXCHANGE_TEST_ENDPOINT,
            NbpTestsUtils.MAX_ATTEMPTS_PER_REQUEST,
            1));
        assertThatThrownBy(() -> unknownRateProvider.getCurrentRate(TEST_CURRENCY_TO))
            .isInstanceOf(UnknownServerException.class)
            .hasMessageContaining("UNKNOWN");
    }

    @Test
    void whenClientWantsToGetExchangeRateAndThereAreSomeProblemsWithServer_thenThrowServerErrorException() {
        NbpRateProvider serverErrorRateProvider = new NbpRateProvider(new NbpRateProvider.Config(
            "http://localhost:" + MOCK_SERVER_PORT + SERVER_ERROR_PATH + GET_EXCHANGE_TEST_ENDPOINT,
            NbpTestsUtils.MAX_ATTEMPTS_PER_REQUEST,
            1));
        assertThatThrownBy(() -> serverErrorRateProvider.getCurrentRate(TEST_CURRENCY_TO))
            .isInstanceOf(ServerErrorException.class)
            .hasMessageContaining("Nbp client call failed");
        wireMockServer.verify(
            exactly(NbpTestsUtils.MAX_ATTEMPTS_PER_REQUEST + 1),
            getRequestedFor(urlPathTemplate(SERVER_ERROR_PATH + GET_EXCHANGE_TEST_ENDPOINT + NbpUtils.GET_RATE_URI)));
    }
}
