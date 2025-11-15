package de.tomsit.example.restservice.infra;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.petstore3.api.PetApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class PetApiTest {

  @Autowired
  PetApi petApi;

  @Test
  void findByStatus_DoesntNotFail() {
    var pets = petApi.findPetsByStatus("available");
    pets.stream().limit(4).forEach(p -> System.out.printf("Pet[%s]: %s%n", p.getId(), p.getName()));

    assertThat(pets).isNotNull();
  }
}
