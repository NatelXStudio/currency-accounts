package com.natelxstudio.currencyaccounts.validation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = IbanValidator.class)
@Retention(RUNTIME)
@ReportAsSingleViolation
public @interface Iban {
    String message() default "userinfo.validation.Iban.message";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
