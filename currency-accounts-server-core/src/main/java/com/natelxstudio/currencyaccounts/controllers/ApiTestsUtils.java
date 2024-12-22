package com.natelxstudio.currencyaccounts.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Random;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.iban4j.CountryCode;
import org.iban4j.Iban;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiTestsUtils {
    public static final Random RANDOM = new Random();
    public static final RandomStringUtils RANDOM_STRING_UTILS = RandomStringUtils.secure();
    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    private static final String API_BASE_PATH = "api/v1";
    static final String ADD_NEW_ACCOUNT = API_BASE_PATH + "/accounts";
    static final String GET_ACCOUNTS_DETAILS = API_BASE_PATH + "/accounts/{id}";
    static final String GET_ACCOUNT_CURRENCY_BALANCE = API_BASE_PATH + "/accounts/{id}/balance/{currency}";
    static final String EXCHANGE = API_BASE_PATH + "/exchange";

    public static String generateIban() {
        Iban iban = new Iban.Builder()
            .leftPadding(true)
            .countryCode(CountryCode.DE)
            .bankCode("66280099")
            .buildRandom();
        return iban.toString();
    }
}
