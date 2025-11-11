package com.example.hello.infra.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
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
    Assertions.assertThat(pojo.getList()).isEmpty();
    Assertions.assertThat(pojo.getSet()).isEmpty();
  }

  @Test
  @SneakyThrows
  public void absentFieldsAreGettingDeserializedAsEmptyList() {
    var pojo = objectMapper.readValue("{}", DummyPojo.class);
    Assertions.assertThat(pojo.getList()).isEmpty();
    Assertions.assertThat(pojo.getSet()).isEmpty();
    Assertions.assertThat(pojo.getPrivateList()).isNull();
  }
}
