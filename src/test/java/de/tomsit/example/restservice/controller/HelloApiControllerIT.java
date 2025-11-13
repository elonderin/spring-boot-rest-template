package de.tomsit.example.restservice.controller;

import static org.hamcrest.Matchers.equalTo;

import de.tomsit.example.restservice.model.HelloResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersSpec;

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
                 .value(HelloResponse::getMessage,
                        equalTo(HelloApiController.buildMessage("public", "anonymousUser")));
  }

  @Nested
  class AdminHelloTests {

    private final RequestHeadersSpec<?> adminHelloReq = webTestClient.get()
                                                                     .uri("/admin/hello");

    @Test
    void withoutAuth_shouldReturnUnauthorized() {
      adminHelloReq
          .exchange()
          .expectStatus().isUnauthorized();
    }

    @Test
    void withUserAuth_shouldReturnForbidden() {
      adminHelloReq
          .headers(h -> h.setBasicAuth("user", "password"))
          .exchange()
          .expectStatus().isForbidden();
    }

    @Test
    void withAdminAuth_shouldReturnText() {
      adminHelloReq
          .headers(h -> h.setBasicAuth("admin", "adminpass"))
          .exchange()
          .expectStatus().isOk()
          .expectBody(HelloResponse.class)
          .value(HelloResponse::getMessage,
                 equalTo(HelloApiController.buildMessage("admin", "admin")));
    }

  }


}
