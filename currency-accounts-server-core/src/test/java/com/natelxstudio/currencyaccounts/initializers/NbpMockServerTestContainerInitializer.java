package com.natelxstudio.currencyaccounts.initializers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;

public class NbpMockServerTestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final String WIREMOCK_CONTAINER_IMAGE_NAME = "wiremock/wiremock:3.8.0";
    private static final String NBP_MOCKS_FOLDER = "/nbp-mock/";
    private static final String MAPPINGS_DIR = "/home/wiremock/mappings/";
    private static final int WIREMOCK_PORT = 8080;

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
        DockerImageName dockerImageName = DockerImageName.parse(WIREMOCK_CONTAINER_IMAGE_NAME);
        GenericContainer<?> wireMockContainer = new GenericContainer<>(dockerImageName)
            .withNetworkAliases("node-plugin-mock")
            .withNetwork(Network.newNetwork())
            .withCommand("--disable-banner --global-response-templating")
            .withExposedPorts(WIREMOCK_PORT)
            .waitingFor(Wait
                .forHttp("/__admin/health")
                .withMethod("GET")
                .forStatusCode(200));
        copyMappingsToContainer(wireMockContainer);
        wireMockContainer.start();

        String baseUrl = String.format("http://%s:%d", wireMockContainer.getHost(), wireMockContainer.getMappedPort(WIREMOCK_PORT));
        TestPropertyValues
            .of()
            .and("nbp.client.base-url=" + baseUrl)
            .applyTo(applicationContext);
    }

    private void copyMappingsToContainer(GenericContainer<?> wireMockContainer) {
        try {
            getListOfMocks()
                .forEach(fileName -> {
                    try {
                        wireMockContainer.withCopyToContainer(
                            Transferable.of(readJsonFromFile(fileName)),
                            MAPPINGS_DIR + fileName);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        } catch (Exception e) {
            throw new RuntimeException("Cannot load or copy mapping files for nbp mock server", e);
        }
    }

    private List<String> getListOfMocks() throws IOException {
        try (
            InputStream in = NbpMockServerTestContainerInitializer.class.getResourceAsStream(NBP_MOCKS_FOLDER);
            BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(in)))
        ) {
            return br.lines().collect(Collectors.toList());
        }
    }

    private String readJsonFromFile(String fileName) throws IOException {
        try (
            InputStream in = NbpMockServerTestContainerInitializer.class.getResourceAsStream(NBP_MOCKS_FOLDER + fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(in)))
        ) {
            return br.lines().collect(Collectors.joining());
        }
    }
}
