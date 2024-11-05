package com.example.bookreadingonline.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

public class MyCollectionUtils {

  public static <T, C extends Collection<T>, R extends Collection<T>> R filter(
      C collection,
      Predicate<? super T> predicate,
      Collector<? super T, ?, R> collector
  ) {
    return collection == null ? null : collection.stream()
        .filter(predicate)
        .collect(collector);
  }

  public static <T, D, C extends Collection<T>, R extends Collection<D>> R map(
      C collection,
      Function<? super T, ? extends D> mapper,
      Collector<? super D, ?, R> collector
  ) {
    return collection == null ? null : collection.stream()
        .map(mapper)
        .collect(collector);
  }

  public static <T, C extends Collection<T>, R extends Collection<T>> R sorted(
      C collection,
      Comparator<? super T> comparator,
      Collector<? super T, ?, R> collector
  ) {
    return collection == null ? null : collection.stream()
        .sorted(comparator)
        .collect(collector);
  }

}
