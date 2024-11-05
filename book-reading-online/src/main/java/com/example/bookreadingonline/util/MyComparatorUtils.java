package com.example.bookreadingonline.util;

import java.util.Comparator;
import java.util.function.Function;

public class MyComparatorUtils {

  public static <T> Comparator<T> compareExceptionClass(
      Function<? super T, Class<? extends Exception>> keyExtractor) {
    return (o1, o2) -> compareExceptionClass(keyExtractor.apply(o1), keyExtractor.apply(o2));
  }

  public static int compareExceptionClass(Class<? extends Exception> c1,
      Class<? extends Exception> c2) {
    return c1.isAssignableFrom(c2) ? -1 : c2.isAssignableFrom(c1) ? 1 : 0;
  }

}
