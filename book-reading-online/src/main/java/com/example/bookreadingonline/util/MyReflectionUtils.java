package com.example.bookreadingonline.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;

public class MyReflectionUtils {

  public static <T> Constructor<? super T> getConstructor(Class<T> clazz,
      Class<?>... parameterTypes) {
    try {
      Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
      constructor.setAccessible(true);
      return constructor;
    } catch (NoSuchMethodException e) {
      Class<? super T> parent = clazz.getSuperclass();
      if (parent != null) {
        return getConstructor(parent, parameterTypes);
      } else {
        return null;
      }
    }
  }

  public static List<Field> getAllFields(Class<?> clazz) {
    List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
    fields.forEach(field -> field.setAccessible(true));
    Class<?> parent = clazz.getSuperclass();
    if (Objects.nonNull(parent)) {
      fields.addAll(getAllFields(parent));
    }
    return fields;
  }

  public static List<Method> getAllMethods(Class<?> clazz) {
    List<Method> methods;
    if (Proxy.isProxyClass(clazz)) {
      methods = Arrays.stream(clazz.getInterfaces())
          .map(MyReflectionUtils::getAllMethods)
          .flatMap(Collection::stream)
          .collect(Collectors.toList());
    } else {
      methods = new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods()));
      Class<?> parent = clazz.getSuperclass();
      if (Objects.nonNull(parent)) {
        methods.addAll(getAllMethods(parent));
      }
    }
    methods.forEach(method -> method.setAccessible(true));
    return methods;
  }

  public static Field getFieldByName(Class<?> clazz, String fieldName) {
    return getAllFields(clazz).stream()
        .filter(field -> field.getName().equals(MyStringUtils.toCamelCase(fieldName)))
        .findFirst()
        .orElse(null);
  }

  public static Field getFieldByJsonProperty(Class<?> clazz, String propertyName) {
    return getAllFields(clazz).stream()
        .filter(field -> field.isAnnotationPresent(JsonProperty.class))
        .filter(field -> field.getAnnotation(JsonProperty.class).value().equals(propertyName))
        .findFirst()
        .orElse(null);
  }

  public static Field getFieldByColumn(Class<?> clazz, String columnName) {
    return getAllFields(clazz).stream()
        .filter(field -> {
          if (field.isAnnotationPresent(Column.class)) {
            return field.getAnnotation(Column.class).name().equals(columnName);
          } else if (field.isAnnotationPresent(JoinColumn.class)) {
            return field.getAnnotation(JoinColumn.class).name().equals(columnName);
          } else {
            return false;
          }
        })
        .findFirst()
        .orElse(null);
  }

  public static List<Field> getFieldsByAnnotation(Class<?> clazz,
      Class<? extends Annotation> annotationClass) {
    return getAllFields(clazz).stream()
        .filter(field -> field.isAnnotationPresent(annotationClass))
        .collect(Collectors.toList());
  }

  public static Field getField(Class<?> clazz, String name) {
    Field field = getFieldByName(clazz, name);
    if (Objects.isNull(field)) {
      field = getFieldByJsonProperty(clazz, name);
    }
    if (Objects.isNull(field)) {
      field = getFieldByColumn(clazz, name);
    }
    return field;
  }

  public static Method getMethodByName(Class<?> clazz, String methodName) {
    return getAllMethods(clazz).stream()
        .filter(method -> method.getName().equals(MyStringUtils.toCamelCase(methodName)) &&
            method.getParameterCount() == 0)
        .findFirst()
        .orElse(null);
  }

  public static Method getGetterMethod(Class<?> clazz, String fieldName) {
    return getMethodByName(clazz, "get" + MyStringUtils.toCaseFormat(fieldName, UPPER_CAMEL));
  }

  public static Method getMethodByJsonProperty(Class<?> clazz, String propertyName) {
    return getAllMethods(clazz).stream()
        .filter(method -> method.isAnnotationPresent(JsonProperty.class))
        .filter(method -> method.getAnnotation(JsonProperty.class).value().equals(propertyName))
        .findFirst()
        .orElse(null);
  }

  public static Method getMethodByColumn(Class<?> clazz, String columnName) {
    return getAllMethods(clazz).stream()
        .filter(method -> {
          if (method.isAnnotationPresent(Column.class)) {
            return method.getAnnotation(Column.class).name().equals(columnName);
          } else if (method.isAnnotationPresent(JoinColumn.class)) {
            return method.getAnnotation(JoinColumn.class).name().equals(columnName);
          } else {
            return false;
          }
        })
        .findFirst()
        .orElse(null);
  }

  public static Method getMethod(Class<?> clazz, String name) {
    Method method = getMethodByName(clazz, name);
    if (Objects.isNull(method)) {
      method = getGetterMethod(clazz, name);
    }
    if (Objects.isNull(method)) {
      method = getMethodByJsonProperty(clazz, name);
    }
    if (Objects.isNull(method)) {
      method = getMethodByColumn(clazz, name);
    }
    return method;
  }

  public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
    Method method;
    try {
      method = clazz.getDeclaredMethod(name, parameterTypes);
      method.setAccessible(true);
      return method;
    } catch (NoSuchMethodException e) {
      Class<?> parent = clazz.getSuperclass();
      if (parent != null) {
        return getMethod(parent, name, parameterTypes);
      } else {
        return null;
      }
    }
  }

  @SneakyThrows
  public static Object getValueByFieldName(Object object, String fieldName) {
    Field field = getFieldByName(object.getClass(), fieldName);
    if (Objects.nonNull(field)) {
      return field.get(object);
    } else {
      throw new NoSuchFieldException("Not found field: " + fieldName +
          " in class: " + object.getClass());
    }
  }

  @SneakyThrows
  public static Object getValueByMethodName(Object object, String methodName) {
    Method method = getMethodByName(object.getClass(), methodName);
    if (Objects.nonNull(method)) {
      return method.invoke(object);
    } else {
      throw new NoSuchMethodException("Not found method: " + methodName +
          " in class " + object.getClass());
    }
  }

  @SneakyThrows
  public static Object getValueByJsonProperty(Object object, String propertyName) {
    Field field = getFieldByJsonProperty(object.getClass(), propertyName);
    if (Objects.nonNull(field)) {
      return field.get(object);
    }
    Method method = getMethodByJsonProperty(object.getClass(), propertyName);
    if (Objects.nonNull(method)) {
      return method.invoke(object);
    }
    throw new ReflectiveOperationException("Not found json property: " + propertyName +
        " in class: " + object.getClass());
  }

  @SneakyThrows
  public static Object getValueByColumn(Object object, String columnName) {
    Field field = getFieldByColumn(object.getClass(), columnName);
    if (Objects.nonNull(field)) {
      return field.get(object);
    }
    Method method = getMethodByColumn(object.getClass(), columnName);
    if (Objects.nonNull(method)) {
      return method.invoke(object);
    }
    throw new ReflectiveOperationException("Not found column: " + columnName +
        " in class: " + object.getClass());
  }

  @SneakyThrows
  public static Object getValue(Object object, String name) {
    Field field = getField(object.getClass(), name);
    if (Objects.nonNull(field)) {
      return field.get(object);
    }
    Method method = getMethod(object.getClass(), name);
    if (Objects.nonNull(method)) {
      return method.invoke(object);
    }
    throw new ReflectiveOperationException("Not found field or method: " + name +
        " in class: " + object.getClass());
  }

  @SneakyThrows
  public static Object getValue(Object object, Field field) {
    return field.get(object);
  }

  @SneakyThrows
  public static Object invoke(Object object, Method method) {
    return method.invoke(object);
  }

}
