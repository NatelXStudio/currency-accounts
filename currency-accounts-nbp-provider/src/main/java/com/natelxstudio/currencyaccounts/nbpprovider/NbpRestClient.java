package com.natelxstudio.currencyaccounts.nbpprovider;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

import com.natelxstudio.currencyaccounts.nbpprovider.exceptions.BadRequestException;
import com.natelxstudio.currencyaccounts.nbpprovider.exceptions.ForbiddenException;
import com.natelxstudio.currencyaccounts.nbpprovider.exceptions.NbpServerException;
import com.natelxstudio.currencyaccounts.nbpprovider.exceptions.NotFoundException;
import com.natelxstudio.currencyaccounts.nbpprovider.exceptions.ServerErrorException;
import com.natelxstudio.currencyaccounts.nbpprovider.exceptions.UnauthorizedException;
import com.natelxstudio.currencyaccounts.nbpprovider.exceptions.UnknownServerException;
import com.natelxstudio.currencyaccounts.nbpprovider.model.GetRateResponseDto;
import com.natelxstudio.currencyaccounts.ratestore.model.Currency;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
class NbpRestClient {
    private final WebClient webClient;
    private final long maxAttemptsPerRequest;
    private final long delayBetweenAttemptsInMillis;

    public NbpRestClient(
        String clientBaseUrl,
        long maxAttemptsPerRequest,
        long delayBetweenAttemptsInMillis
    ) {
        this.webClient = WebClient.builder()
            .baseUrl(clientBaseUrl)
            .build();
        this.maxAttemptsPerRequest = maxAttemptsPerRequest;
        this.delayBetweenAttemptsInMillis = delayBetweenAttemptsInMillis;
    }

    GetRateResponseDto getExchangeRate(Currency currencyFrom) throws ServerErrorException {
        return this.webClient
            .get()
            .uri(NbpUtils.GET_RATE_URI, currencyFrom.toString().toLowerCase())
            .header(HttpHeaders.ACCEPT, APPLICATION_JSON.toString())
            .exchangeToMono(response -> handleResponse(
                response,
                "Cannot get rate for: " + currencyFrom + " with nbp client"))
            .retryWhen(Retry
                .fixedDelay(maxAttemptsPerRequest, Duration.ofMillis(delayBetweenAttemptsInMillis))
                .filter(NbpRestClient::isServerOrClientError)
                .onRetryExhaustedThrow((spec, signal) -> {
                    throw handleRetryExhaustedThrow(signal);
                }))
            .block();
    }

    private Mono<GetRateResponseDto> handleResponse(
        ClientResponse response,
        String messagePrefix
    ) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(GetRateResponseDto.class);
        } else {
            return handleErrorResponse(response, messagePrefix);
        }
    }

    private Mono<GetRateResponseDto> handleErrorResponse(
        ClientResponse response,
        String messagePrefix
    ) {
        if (response.statusCode().is5xxServerError()) {
            log.error("Nbp returned SERVER ERROR with status code: {}", response.statusCode().value());
            return Mono.error(new NbpServerException(messagePrefix + ": SERVER ERROR"));
        } else {
            if (response.statusCode().value() == HttpStatus.BAD_REQUEST.value()) {
                return response.bodyToMono(String.class).flatMap(it -> Mono.error(new BadRequestException(messagePrefix + ": BAD REQUEST. Response: " + it)));
            } else if (response.statusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
                return Mono.error(new UnauthorizedException(messagePrefix + ": UNAUTHORIZED, check authorization configuration"));
            } else if (response.statusCode().value() == HttpStatus.FORBIDDEN.value()) {
                return response.bodyToMono(String.class).flatMap(it -> Mono.error(new ForbiddenException(messagePrefix + ": FORBIDDEN, check nbp permissions. Response: " + it)));
            } else if (response.statusCode().value() == HttpStatus.NOT_FOUND.value()) {
                return response.bodyToMono(String.class).flatMap(it -> Mono.error(new NotFoundException(messagePrefix + ": NOT FOUND. Response: " + it)));
            }
            return Mono.error(new UnknownServerException(messagePrefix + ": UNKNOWN ERROR with status code: " + response.statusCode().value()));
        }
    }

    private ServerErrorException handleRetryExhaustedThrow(
        Retry.RetrySignal signal
    ) {
        log.error("Retrying failed due to: {}", signal.failure().toString());
        return new ServerErrorException(String.format(
            "Nbp client call failed after retrying %d times",
            signal.totalRetries()));
    }

    private static boolean isServerOrClientError(Throwable throwable) {
        return throwable instanceof NbpServerException || throwable instanceof WebClientRequestException;
    }
}
