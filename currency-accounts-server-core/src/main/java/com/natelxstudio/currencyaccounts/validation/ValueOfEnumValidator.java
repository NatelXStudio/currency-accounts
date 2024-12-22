package com.natelxstudio.currencyaccounts.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, CharSequence> {
    private List<String> acceptedValues;
    private Pattern pattern;

    @Override
    public void initialize(ValueOfEnum annotation) {
        Stream<String> acceptedValuesStream = Stream.of(annotation.enumClass()
                .getEnumConstants())
            .map(Enum::name);

        if (!annotation.regexp().isEmpty()) {
            try {
                pattern = Pattern.compile(annotation.regexp());
            } catch (PatternSyntaxException e) {
                throw new IllegalArgumentException("Given regex is invalid", e);
            }
            acceptedValuesStream = acceptedValuesStream.filter(it -> pattern.matcher(it).matches());
        }

        acceptedValues = acceptedValuesStream.toList();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return acceptedValues.contains(value.toString());
    }
}
