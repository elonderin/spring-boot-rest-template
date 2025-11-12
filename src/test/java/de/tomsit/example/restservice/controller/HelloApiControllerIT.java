package de.tomsit.example.restservice.controller;

import static org.assertj.core.api.Assertions.assertThat;

import de.tomsit.example.restservice.RestAssuredConfig;
import de.tomsit.example.restservice.model.HelloResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(RestAssuredConfig.class)
class HelloApiControllerIT {


  @Test
  void testPublicHello() {
    HelloResponse response = RestAssured.given()
                                        .when()
                                        .get("/public/hello")
                                        .then()
                                        .statusCode(200)
                                        .extract()
                                        .as(HelloResponse.class);

    assertThat(response).returns(HelloApiController.PUBLIC_HELLO_WORLD, HelloResponse::getMessage);
  }

  @Test
  void testAdminHello() {
    HelloResponse response = RestAssured.given()
                                        .auth().preemptive().basic("admin", "adminpass")
                                        .when()
                                        .get("/admin/hello")
                                        .then()
                                        .statusCode(200)
                                        .extract()
                                        .as(HelloResponse.class);

    assertThat(response).returns(HelloApiController.ADMIN_HELLO_WORLD, HelloResponse::getMessage);
  }
}
