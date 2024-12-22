package com.natelxstudio.currencyaccounts.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.iban4j.IbanUtil;

public class IbanValidator implements ConstraintValidator<Iban, String> {
    @Override
    public boolean isValid(String iban, ConstraintValidatorContext context) {
        if (iban == null) {
            return false;
        }
        return IbanUtil.isValid(iban);
    }
}
