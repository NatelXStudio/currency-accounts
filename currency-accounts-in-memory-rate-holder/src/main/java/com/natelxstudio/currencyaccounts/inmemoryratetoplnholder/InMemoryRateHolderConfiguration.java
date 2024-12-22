package com.natelxstudio.currencyaccounts.inmemoryratetoplnholder;

import com.natelxstudio.currencyaccounts.ratestore.ports.RateToPlnHolderPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class InMemoryRateHolderConfiguration {
    @Bean
    RateToPlnHolderPort rateToPlnHolderPort() {
        return new InMemoryRateToPlnHolder();
    }
}
