package de.tomsit.example.restservice.controller;

import de.tomsit.example.restservice.api.HelloApi;
import de.tomsit.example.restservice.model.HelloResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloApiController implements HelloApi {

  @Override
  public ResponseEntity<HelloResponse> getHello() {
    HelloResponse response = new HelloResponse();
    response.setMessage("Hello, World!");
    return ResponseEntity.ok(response);
  }
}
