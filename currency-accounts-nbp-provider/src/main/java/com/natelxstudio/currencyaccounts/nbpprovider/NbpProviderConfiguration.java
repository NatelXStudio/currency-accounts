package com.natelxstudio.currencyaccounts.nbpprovider;

import com.natelxstudio.currencyaccounts.ratestore.ports.RateProviderPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class NbpProviderConfiguration {
    @Bean
    RateProviderPort rateProviderPort(
        @Value("${nbp.client.base-url}") String clientBaseUrl,
        @Value("${nbp.client.max-attempts-per-request}") long maxAttemptsPerRequest,
        @Value("${nbp.client.delay-between-attempts-in-millis}") long delayBetweenAttemptsInMillis
    ) {
        return new NbpRateProvider(new NbpRateProvider.Config(
            clientBaseUrl,
            maxAttemptsPerRequest,
            delayBetweenAttemptsInMillis));
    }
}
