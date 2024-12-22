package com.natelxstudio.currencyaccounts.controllers;

import com.natelxstudio.currencyaccounts.accountsstore.AccountsStoreFacade;
import com.natelxstudio.currencyaccounts.accountsstore.model.Currency;
import com.natelxstudio.currencyaccounts.mappers.GetAccountDetailsResponseDtoMapper;
import com.natelxstudio.currencyaccounts.model.GetAccountBalanceResponseDto;
import com.natelxstudio.currencyaccounts.model.GetAccountDetailsResponseDto;
import com.natelxstudio.currencyaccounts.model.InsertBankAccountRequestDto;
import com.natelxstudio.currencyaccounts.model.InsertBankAccountResponseDto;
import com.natelxstudio.currencyaccounts.validation.ValueOfEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/accounts")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountsController {
    private final AccountsStoreFacade accountsStoreFacade;

    //This should be kafka/rabbitmq listener to ensure synchronization, not rest controller
    @PostMapping
    public ResponseEntity<InsertBankAccountResponseDto> addNewAccount(
        @Valid @RequestBody InsertBankAccountRequestDto insertBankAccountRequestDto
    ) {
        log.info("Start processing accounts POST request: payload = {}", insertBankAccountRequestDto);
        InsertBankAccountResponseDto response = new InsertBankAccountResponseDto(
            accountsStoreFacade.insertAccount(
                insertBankAccountRequestDto.iban(),
                insertBankAccountRequestDto.firstName(),
                insertBankAccountRequestDto.lastName(),
                Currency.PLN,
                insertBankAccountRequestDto.initialBalance()).toString());
        log.info("Finished processing accounts POST request with response: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetAccountDetailsResponseDto> getAccountDetails(
        @PathVariable(value = "id") @UUID(message = "Id should has correct UUID format.") String id
    ) {
        log.info("Start processing accounts GET request: id = {}", id);
        GetAccountDetailsResponseDto response = GetAccountDetailsResponseDtoMapper.toDto(
            accountsStoreFacade.getAccount(java.util.UUID.fromString(id)));
        log.info("Finished processing accounts GET request with response: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/balance/{currency}")
    public ResponseEntity<GetAccountBalanceResponseDto> getAccountCurrencyBalance(
        @PathVariable(value = "id") @UUID(message = "Id should has correct UUID format.") String id,
        @PathVariable(value = "currency") @ValueOfEnum(enumClass = Currency.class, message = "Invalid currency name.") String currency
    ) {
        log.info("Start processing accounts/balance GET request: id = {}, currency = {}", id, currency);
        GetAccountBalanceResponseDto response = new GetAccountBalanceResponseDto(
            accountsStoreFacade.getBalance(java.util.UUID.fromString(id), Currency.valueOf(currency)));
        log.info("Finished processing accounts/balance GET request with response: {}", response);
        return ResponseEntity.ok(response);
    }
}
