package com.natelxstudio.currencyaccounts.ratestore;

import com.natelxstudio.currencyaccounts.ratestore.ports.RateProviderPort;
import com.natelxstudio.currencyaccounts.ratestore.ports.RateToPlnHolderPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RateStoreConfiguration {
    @Bean
    RateStoreFacade rateStoreFacade(
        RateProviderPort rateProviderPort,
        RateToPlnHolderPort rateToPlnHolderPort
    ) {
        return new RateStoreFacade(
            new RefreshRatesService(rateProviderPort, rateToPlnHolderPort),
            new GetRateService(rateToPlnHolderPort));
    }
}
