package se.omegapoint.controllers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdditionHandlerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void whenValidRequest_thenReturns200() {
        String requestBody = "{ \"numberOne\": 5, \"numberTwo\": 10 }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .param("syncResult", true)
                .when()
                .post("/public/addition/add")
                .then()
                .statusCode(200)
                .body("result", equalTo(15));
    }

    @Test
    public void invalidRequest_thenReturns400() {
        String requestBody = "{ \"numberOne\": , \"numberTwo\": 10 }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .param("syncResult", true)
                .when()
                .post("/public/addition/add")
                .then()
                .statusCode(400);
    }
}