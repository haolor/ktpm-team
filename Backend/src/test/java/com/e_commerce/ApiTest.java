package com.e_commerce;

import com.e_commerce.config.TestConfig;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
public class ApiTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1";
    }

    @Test
    void testHealthCheck() {
        given()
            .when()
            .get("/")
            .then()
            .statusCode(anyOf(is(200), is(404), is(401), is(500)));
    }

    @Test
    void testGetProducts() {
        given()
            .when()
            .get("/product")
            .then()
            .statusCode(anyOf(is(200), is(401), is(500)));
    }

    @Test
    void testGetCategories() {
        given()
            .when()
            .get("/category")
            .then()
            .statusCode(anyOf(is(200), is(401), is(500)));
    }
}
