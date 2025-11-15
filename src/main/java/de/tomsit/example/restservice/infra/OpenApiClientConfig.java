package de.tomsit.example.restservice.infra;

import io.swagger.petstore3.api.PetApi;
import io.swagger.petstore3.client.ApiClient;
import io.swagger.petstore3.client.RFC3339DateFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class OpenApiClientConfig {

  @Bean
  public ApiClient apiClient(RestClient.Builder restClientBuilder) {
    ApiClient client = new ApiClient(
        RestClient.builder().build()
        , null
        , new RFC3339DateFormat()
    );

    client.setBasePath("https://petstore3.swagger.io/api/v3/pet");

    return client;
  }

  @Bean
  public PetApi petApi(ApiClient apiClient) {
    return new PetApi(apiClient);
  }
}
