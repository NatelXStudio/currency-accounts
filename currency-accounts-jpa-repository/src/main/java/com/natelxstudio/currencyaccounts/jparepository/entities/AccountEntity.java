package com.natelxstudio.currencyaccounts.jparepository.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AccountEntity {
    @Id
    private UUID id;

    @Column(name = "iban", nullable = false, length = 33)
    private String iban;

    @Column(name = "first_name", nullable = false, length = 30)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "created_timestamp", nullable = false)
    private long createdTimestamp;
}
