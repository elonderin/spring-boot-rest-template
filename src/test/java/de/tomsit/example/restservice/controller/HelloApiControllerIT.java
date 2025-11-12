package de.tomsit.example.restservice.controller;

import static org.assertj.core.api.Assertions.assertThat;

import de.tomsit.example.restservice.model.HelloResponse;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class HelloApiControllerIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void testPublicHello() {
    assertThat(getHelloFrom("/public/hello"))
        .returns(HelloApiController.PUBLIC_HELLO_WORLD, HelloResponse::getMessage);
  }

  @Test
  void testAdminHello() {
    restTemplate = restTemplate.withBasicAuth("admin", "adminpass");

    assertThat(getHelloFrom("/admin/hello"))
        .returns(HelloApiController.ADMIN_HELLO_WORLD, HelloResponse::getMessage);
  }

  private HelloResponse getHelloFrom(String path) {
    return restTemplate.getForObject(path, HelloResponse.class, Map.of());
  }

}
