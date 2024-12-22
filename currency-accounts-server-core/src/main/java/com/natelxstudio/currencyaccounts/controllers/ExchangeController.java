package com.natelxstudio.currencyaccounts.controllers;

import com.natelxstudio.currencyaccounts.accountsstore.AccountsStoreFacade;
import com.natelxstudio.currencyaccounts.accountsstore.model.Currency;
import com.natelxstudio.currencyaccounts.model.ExchangeRequestDto;
import com.natelxstudio.currencyaccounts.ratestore.RateStoreFacade;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/exchange")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ExchangeController {
    private final RateStoreFacade rateStoreFacade;
    private final AccountsStoreFacade accountsStoreFacade;

    //This should be kafka/rabbitmq listener to ensure synchronization, not rest controller
    @PostMapping
    public ResponseEntity<Void> exchange(
        @Valid @RequestBody ExchangeRequestDto exchangeRequestDto
    ) {
        log.info("Start processing transactions POST request: payload = {}", exchangeRequestDto);
        accountsStoreFacade.exchangeFunds(
            UUID.fromString(exchangeRequestDto.id()),
            Currency.valueOf(exchangeRequestDto.fromCurrency()),
            Currency.valueOf(exchangeRequestDto.toCurrency()),
            exchangeRequestDto.amount(),
            (fromCurrencyArg, toCurrencyArg) -> {
                com.natelxstudio.currencyaccounts.ratestore.model.Currency localFromCurrency =
                    com.natelxstudio.currencyaccounts.ratestore.model.Currency.valueOf(fromCurrencyArg.toString());
                com.natelxstudio.currencyaccounts.ratestore.model.Currency localToCurrency =
                    com.natelxstudio.currencyaccounts.ratestore.model.Currency.valueOf(toCurrencyArg.toString());
                return rateStoreFacade.getRate(localFromCurrency, localToCurrency);
            });
        log.info("Finished processing transactions POST request");
        return ResponseEntity.noContent().build();
    }
}
