package com.natelxstudio.currencyaccounts.mappers;

import com.natelxstudio.currencyaccounts.accountsstore.model.Account;
import com.natelxstudio.currencyaccounts.model.GetAccountDetailsResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetAccountDetailsResponseDtoMapper {
    public static GetAccountDetailsResponseDto toDto(Account account) {
        return new GetAccountDetailsResponseDto(
            account.id().toString(),
            account.iban(),
            account.firstName(),
            account.lastName(),
            account.createdTimestamp());
    }
}
