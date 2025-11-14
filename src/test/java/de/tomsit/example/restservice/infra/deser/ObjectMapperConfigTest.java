package de.tomsit.example.restservice.infra.deser;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tomsit.example.restservice.infra.ObjectMapperConfig;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;

@JsonTest
@Import(ObjectMapperConfig.class)
class ObjectMapperConfigTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Data
  static class DummyPojo {

    List<String> list;
    Set<String> set;
    String someOtherProperty;

    @Getter(value = AccessLevel.PRIVATE)
    List<String> privateList;
  }

  @Test
  @SneakyThrows
  public void nullValuesAreGettingDeserializedAsEmptyList() {
    var pojo = objectMapper.readValue("{ \"list\": null, \"set\": null}", DummyPojo.class);
    assertThat(pojo.getList()).isEmpty();
    assertThat(pojo.getSet()).isEmpty();
  }

  @Test
  @SneakyThrows
  public void absentFieldsAreGettingDeserializedAsEmptyList() {
    var pojo = objectMapper.readValue("{}", DummyPojo.class);
    assertThat(pojo.getList()).isEmpty();
    assertThat(pojo.getSet()).isEmpty();
    assertThat(pojo.getPrivateList()).isNull();
  }
}
