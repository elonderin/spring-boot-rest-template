package de.tomsit.example.restservice.controller;

import de.tomsit.example.restservice.api.HelloApi;
import de.tomsit.example.restservice.model.HelloResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloApiController implements HelloApi {

  public static final String PUBLIC_HELLO_WORLD = "public: Hello, World!";
  public static final String ADMIN_HELLO_WORLD = "admin: Hello, World!";

  @Override
  public ResponseEntity<HelloResponse> publicHello() {
    return returnHello(PUBLIC_HELLO_WORLD);
  }

  @Override
  public ResponseEntity<HelloResponse> adminHello() {
    return returnHello(ADMIN_HELLO_WORLD);
  }

  private ResponseEntity<HelloResponse> returnHello(String message) {
    HelloResponse response = new HelloResponse();
    response.setMessage(message);
    return ResponseEntity.ok(response);
  }
}
