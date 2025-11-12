package de.tomsit.example.restservice.controller;

import static org.hamcrest.Matchers.equalTo;

import de.tomsit.example.restservice.model.HelloResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class HelloApiControllerIT {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void testPublicHello() {
    webTestClient.get()
                 .uri("/public/hello")
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(HelloResponse.class)
                 .value(HelloResponse::getMessage, equalTo(HelloApiController.PUBLIC_HELLO_WORLD));
  }

  @Test
  void testAdminHello() {
    webTestClient.get()
                 .uri("/admin/hello")
                 .headers(h -> h.setBasicAuth("admin", "adminpass"))
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(HelloResponse.class)
                 .value(HelloResponse::getMessage, equalTo(HelloApiController.ADMIN_HELLO_WORLD));
  }
}
