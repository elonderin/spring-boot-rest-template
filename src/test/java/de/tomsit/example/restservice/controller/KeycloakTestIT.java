package de.tomsit.example.restservice.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KeycloakTestIT {

  @Autowired
  private WebTestClient webTestClient;


  @Test
  void adminCanAccessAdminEndpoint() {
    var token = getAccessToken("admin", "admin");

    webTestClient.get()
                 .uri("/admin/hello")
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                 .exchange()
                 .expectStatus().is2xxSuccessful()
                 .expectBody(String.class)
                 .value(body -> assertThat(body).isNotBlank());
  }


  @Test
  void userCannotAccessAdminEndpoint() {
    var token = getAccessToken("user", "user");

    webTestClient.get()
                 .uri("/admin/hello")
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                 .exchange()
                 .expectStatus().isForbidden();
  }


  public String getAccessToken(String username, String password) {
    var rest = new RestTemplate();

    var url = "http://localhost:9080/realms/test-realm/protocol/openid-connect/token";

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    var form = new org.springframework.util.LinkedMultiValueMap<String, String>();
    form.add("grant_type", "password");
    form.add("client_id", "test-client");
    form.add("username", username);
    form.add("password", password);

    var response = rest.postForEntity(url, new HttpEntity<>(form, headers), String.class);

    try {
      var json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(response.getBody());
      return json.get("access_token").asText();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
