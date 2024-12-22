package com.natelxstudio.currencyaccounts;

import com.natelxstudio.currencyaccounts.initializers.NbpMockServerTestContainerInitializer;
import com.natelxstudio.currencyaccounts.initializers.PostgresTestContainerInitializer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(initializers = {
    PostgresTestContainerInitializer.class,
    NbpMockServerTestContainerInitializer.class
})
@SpringBootTest(
    classes = MainApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegrationTest {
}
