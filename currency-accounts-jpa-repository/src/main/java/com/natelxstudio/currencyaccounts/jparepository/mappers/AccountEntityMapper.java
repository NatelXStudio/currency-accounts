package com.natelxstudio.currencyaccounts.jparepository.mappers;

import com.natelxstudio.currencyaccounts.accountsstore.model.Account;
import com.natelxstudio.currencyaccounts.jparepository.entities.AccountEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountEntityMapper {
    public static Account toDomainObject(AccountEntity entity) {
        return new Account(
            entity.getId(),
            entity.getIban(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getCreatedTimestamp());
    }

    public static AccountEntity toEntity(Account account) {
        return new AccountEntity(
            account.id(),
            account.iban(),
            account.firstName(),
            account.lastName(),
            account.createdTimestamp());
    }
}
