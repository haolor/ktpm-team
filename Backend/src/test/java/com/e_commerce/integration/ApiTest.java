package com.e_commerce.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ApiTest extends IntegrationTestBase {

    @Test
    void healthEndpointResponds() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/actuator/health", String.class);
        assertThat(response).isNotNull();
    }

    @Test
    void productListEndpointResponds() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/products", String.class);
        assertThat(response).isNotNull();
    }
}
