package com.example.hello.infra.jackson;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ObjectMapperConfig {

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer ensureEmptyListOnDeserialization() {
    return builder -> {
      SimpleModule sm = new SimpleModule();
      sm.setDeserializerModifier(new BeanDeserializerModifier() {
        @Override
        public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
          return new DeserializerCleanup(deserializer);
        }
      });
      builder.modulesToInstall(sm);
      builder.postConfigurer(objectMapper ->
          objectMapper
              .configOverride(List.class)
              .setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY)));
    };
  }

  public static class DeserializerCleanup extends DelegatingDeserializer {

    private static final Map<Class<?>, List<FieldInfo>> collectionFieldsByClass = new ConcurrentHashMap<>();

    public DeserializerCleanup(JsonDeserializer<?> delegate) {
      super(delegate);
    }

    @Override
    protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> newDelegate) {
      return new DeserializerCleanup(newDelegate);
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      Object deserializedObject = super.deserialize(p, ctxt);
      ensureEmptyLists(deserializedObject);
      return deserializedObject;
    }

    private void ensureEmptyLists(Object deserializedObject) {
      if (deserializedObject.getClass().isPrimitive()) {
        return;
      }
      collectionFieldsByClass.computeIfAbsent(deserializedObject.getClass(), this::resolveCollectionFields)
          .forEach(field -> {
            try {
              var fieldValue = field.getter.invoke(deserializedObject);
              if (fieldValue == null) {
                field.setter.invoke(deserializedObject, field.collectionSupplier.get());
              }
            } catch (IllegalAccessException | InvocationTargetException e) {
              log.trace("Either reading or setting value failed. Skipping gracefully", e);
            }
          });
    }

    private List<FieldInfo> resolveCollectionFields(Class<?> aClass) {
      log.debug("Computing for: {}", aClass);
      return FieldUtils.getAllFieldsList(aClass).stream()
          .map(this::toFieldInfo)
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    }

    @Nullable
    private FieldInfo toFieldInfo(Field field) {

      if (List.class.isAssignableFrom(field.getType())) {
        return toFieldInfo(field, List::of);
      } else if (Set.class.isAssignableFrom(field.getType())) {
        return toFieldInfo(field, Set::of);
      }
      return null;
    }

    private static <T extends Collection<?>> FieldInfo toFieldInfo(Field field, Supplier<? extends T> collectionConstructor) {
      var fieldNameCapitalized = StringUtils.capitalize(field.getName());
      var getter = MethodUtils.getMatchingAccessibleMethod(field.getDeclaringClass(), "get" + fieldNameCapitalized);
      var setter = MethodUtils.getMatchingAccessibleMethod(field.getDeclaringClass(), "set" + fieldNameCapitalized, field.getType());

      FieldInfo fieldInfo;
      if (getter != null && setter != null) {
        fieldInfo = new FieldInfo(collectionConstructor, getter, setter);
      } else {
        fieldInfo = null;
      }
      log.trace("FieldInfo for: {} = {}", field, fieldInfo);
      return fieldInfo;
    }

    @Value
    static class FieldInfo {

      Supplier<?> collectionSupplier;
      Method getter;
      Method setter;
    }

  }

}
