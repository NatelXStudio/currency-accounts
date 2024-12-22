package com.natelxstudio.currencyaccounts.jparepository.mappers;

import com.natelxstudio.currencyaccounts.accountsstore.model.Transaction;
import com.natelxstudio.currencyaccounts.jparepository.entities.TransactionEntity;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionEntityMapper {
    public static TransactionEntity toEntity(Transaction transaction, UUID accountId) {
        return new TransactionEntity(
            transaction.id(),
            accountId,
            TransactionEntity.Currency.valueOf(transaction.currency().toString()),
            transaction.amount(),
            transaction.createdTimestamp());
    }
}
