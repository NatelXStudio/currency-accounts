package com.natelxstudio.currencyaccounts.model;

import com.natelxstudio.currencyaccounts.validation.Iban;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record InsertBankAccountRequestDto(
    @Iban(message = "Iban should has correct IBAN format.") @NotBlank(message = "Iban cannot be null or empty.") String iban,
    @NotNull(message = "First name cannot be null.") @Pattern(regexp = "^[a-zA-Z]{1,30}$", message = "Invalid first name.") String firstName,
    @NotNull(message = "Last name cannot be null.") @Pattern(regexp = "^[a-zA-Z]{1,50}$", message = "Invalid last name.") String lastName,
    @NotNull(message = "Initial balance cannot be null.")
    @Positive(message = "Initial balance cannot be zero or negative.")
    @Digits(integer = 30, fraction = 2, message = "Invalid initial balance.") BigDecimal initialBalance
) {
}
