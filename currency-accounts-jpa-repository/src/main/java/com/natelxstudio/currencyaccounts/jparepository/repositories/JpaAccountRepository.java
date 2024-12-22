package com.natelxstudio.currencyaccounts.jparepository.repositories;

import com.natelxstudio.currencyaccounts.jparepository.entities.AccountEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAccountRepository extends JpaRepository<AccountEntity, UUID> {
    boolean existsByIban(String iban);
}
