package com.example.bookreadingonline.util;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public interface Generic<T> {

  static <T> Class<T> getClassParam(Class<?> rawType, Class<?> clazz, int index) {
    return TypeToken.of(clazz)
        .getTypes()
        .stream()
        .filter(typeToken -> rawType.equals(typeToken.getRawType()))
        .map(TypeToken::getType)
        .filter(type -> type instanceof ParameterizedType)
        .map(type -> ((ParameterizedType) type).getActualTypeArguments()[index])
        .map(Generic::<T>castFrom)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  static <T> Class<T> getClassParam(Class<?> clazz, int index) {
    return TypeToken.of(clazz)
        .getTypes()
        .stream()
        .filter(typeToken -> Generic.class.equals(typeToken.getRawType()))
        .map(TypeToken::getType)
        .filter(type -> type instanceof ParameterizedType)
        .map(type -> ((ParameterizedType) type).getActualTypeArguments()[index])
        .map(Generic::<T>castFrom)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  @SuppressWarnings("unchecked")
  static <T> Class<T> castFrom(Type type) {
    try {
      return (Class<T>) type;
    } catch (Exception e) {
      return null;
    }
  }

  default Class<T> getClassParam() {
    return Generic.getClassParam(getClass(), 0);
  }

}
