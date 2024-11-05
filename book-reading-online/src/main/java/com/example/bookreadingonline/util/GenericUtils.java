package com.example.bookreadingonline.util;

import com.google.common.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenericUtils {

  public static <T> Class<T> getTypeParameter(Class<?> clazz, int index) {
    return TypeToken.of(clazz)
        .getTypes()
        .stream()
        .map(TypeToken::getType)
        .filter(ParameterizedType.class::isInstance)
        .map(type -> ((ParameterizedType) type).getActualTypeArguments()[index])
        .map(GenericUtils::<T>castFrom)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  public static <T> Class<T> getTypeParameter(Class<?> clazz) {
    return getTypeParameter(clazz, 0);
  }

  @SuppressWarnings("unchecked")
  public static <T> Class<T> castFrom(Type type) {
    try {
      return (Class<T>) type;
    } catch (Exception e) {
      return null;
    }
  }

}
