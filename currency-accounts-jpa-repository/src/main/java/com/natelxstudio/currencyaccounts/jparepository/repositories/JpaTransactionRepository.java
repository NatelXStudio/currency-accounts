package com.natelxstudio.currencyaccounts.jparepository.repositories;

import com.natelxstudio.currencyaccounts.jparepository.entities.TransactionEntity;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaTransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t WHERE t.currency = :currency AND t.accountId = :accountId")
    BigDecimal sumAmountByAccountIdAndCurrency(@Param("accountId") UUID accountId, @Param("currency") TransactionEntity.Currency currency);
}
