package com.example.hello.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.hello.model.HelloResponse;
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
  void getHelloWorld() {

    var response = restTemplate.getForObject("/hello", HelloResponse.class, Map.of());

    assertThat(response)
        .as("spot testing some controller response")
        .returns("Hello, World!", HelloResponse::getMessage);

  }
}
