package de.tomsit.example.restservice.infra;

import io.swagger.petstore3.api.PetApi;
import io.swagger.petstore3.client.ApiClient;
import io.swagger.petstore3.client.RFC3339DateFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class RestApiClientsConfig {

  @Bean
  public PetApi petApi() {
    var apiClient = new ApiClient(
        RestClient.builder().build()
        , null
        , new RFC3339DateFormat()
    );

    apiClient.setBasePath("https://petstore3.swagger.io/api/v3");
    return new PetApi(apiClient);
  }
}
