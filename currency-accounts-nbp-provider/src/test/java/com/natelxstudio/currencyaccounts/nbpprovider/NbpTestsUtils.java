package com.natelxstudio.currencyaccounts.nbpprovider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class NbpTestsUtils {
    static final int MAX_ATTEMPTS_PER_REQUEST = 4;
    static final String BAD_REQUEST_PATH = "/badRequest";
    static final String UNAUTHORIZED_PATH = "/unauthorized";
    static final String FORBIDDEN_PATH = "/forbidden";
    static final String NOT_FOUND_PATH = "/notFound";
    static final String UNKNOWN_ERROR_PATH = "/unknown";
    static final String SERVER_ERROR_PATH = "/serverError";
    static final String GET_RATE_OK_BODY = """
        {
            "table": "A",
            "currency": "dolar amerykański",
            "code": "USD",
            "rates": [
                {
                    "no": "247/A/NBP/2024",
                    "effectiveDate": "2024-12-20",
                    "mid": 4.100
                }
            ]
        }
        """;
    static final String BAD_REQUEST_RESPONSE_BODY = "401 Badrequest - Bad request";
    static final String UNAUTHORIZED_RESPONSE_BODY = "401 Unauthorized - Unauthorized";
    static final String FORBIDDEN_RESPONSE_BODY = "403 Forbidden - Forbidden";
    static final String NOT_FOUND_RESPONSE_BODY = "404 NotFound - Not Found - Brak danych";
    static final String UNKNOWN_RESPONSE_BODY = "410 Gone - Gone";
}
