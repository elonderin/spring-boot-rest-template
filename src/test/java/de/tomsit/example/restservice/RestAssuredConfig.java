package de.tomsit.example.restservice;

import io.restassured.RestAssured;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;

//@ConditionalOnWebApplication
@TestConfiguration
public class RestAssuredConfig {

  @EventListener(WebServerInitializedEvent.class)
  void init(WebServerInitializedEvent event) {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = event.getWebServer().getPort();
  }
}
