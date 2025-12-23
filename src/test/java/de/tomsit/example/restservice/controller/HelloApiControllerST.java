package de.tomsit.example.restservice.controller;

import static org.assertj.core.api.Assertions.assertThat;

import de.tomsit.example.restservice.model.HelloResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = HelloApiController.class)
class HelloApiControllerST {

  @Autowired
  private HelloApiController helloApiController;

  @Test
  void testPublicHello(@Mock Authentication authentication) {

    Mockito.when(authentication.getName()).thenReturn("my-name");

    SecurityContextHolder.getContext().setAuthentication(authentication);

    assertThat(helloApiController.adminHello().getBody())
        .returns(HelloApiController.buildMessage("admin", "my-name"),
                 HelloResponse::getMessage);

  }


}
