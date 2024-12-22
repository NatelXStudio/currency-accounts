package com.natelxstudio.currencyaccounts.initializers;

import lombok.NonNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class PostgresTestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final String POSTGRES_CONTAINER_IMAGE_NAME = "postgres:16.3";

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        PostgreSQLContainer postgresContainer = new PostgreSQLContainer(POSTGRES_CONTAINER_IMAGE_NAME);
        postgresContainer
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withNetworkAliases("postgres")
            .withNetwork(Network.newNetwork())
            .waitingFor(Wait.forListeningPort());
        postgresContainer.start();

        TestPropertyValues.of(
            "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
            "spring.datasource.username=" + postgresContainer.getUsername(),
            "spring.datasource.password=" + postgresContainer.getPassword(),
            "spring.jpa.hibernate.dialect=org.hibernate.dialect.PostgreSqlDialect"
        ).applyTo(applicationContext);
    }
}
