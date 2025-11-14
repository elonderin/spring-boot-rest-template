package de.tomsit.example.restservice.controller;

import de.tomsit.example.restservice.api.HelloApi;
import de.tomsit.example.restservice.model.HelloResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloApiController implements HelloApi {

  @Override
  public ResponseEntity<HelloResponse> publicHello() {
    return returnHello(buildMessage("public", SecurityContextHolder.getContext().getAuthentication().getName()));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public ResponseEntity<HelloResponse> adminHello() {
    return returnHello(buildMessage("admin", SecurityContextHolder.getContext().getAuthentication().getName()));
  }

  private ResponseEntity<HelloResponse> returnHello(String message) {
    var response = new HelloResponse();
    response.setMessage(message);
    return ResponseEntity.ok(response);
  }

  protected static String buildMessage(String path, String user) {
    return "%s: Hello User '%s'!".formatted(path, user);
  }
}
