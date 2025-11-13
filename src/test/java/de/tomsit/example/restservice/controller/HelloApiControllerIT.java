package de.tomsit.example.restservice.controller;

import static org.hamcrest.Matchers.equalTo;

import de.tomsit.example.restservice.model.HelloResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import java.util.List;
import javax.crypto.spec.SecretKeySpec;
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

    @Nested
    class JwtIntegrationTest {

      public static final String JWT_SECRET = "testsecret1234567890testsecret1234567890";


      @Test
      void shouldAccessEndpointWithValidJwt() {
        Key key = new SecretKeySpec(JWT_SECRET.getBytes(), SignatureAlgorithm.HS256.getJcaName());

        var token = Jwts.builder()
                        .setSubject("jwt-user")
                        .claim("scope", "read")
                        .claims().add("roles", List.of("ROLE_ADMIN", "ADMIN"))
                        .and()
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 60000))
                        .signWith(key)
                        .compact();

        adminHelloReq
            .headers(h -> h.setBearerAuth(token))
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody(String.class)
            .consumeWith(result -> System.out.println(result.getResponseBody()));
      }
    }

  }


}
