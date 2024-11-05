package com.example.bookreadingonline.util;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MyListUtils {

  public static <T> List<T> nullIfEmpty(List<T> collection) {
    return CollectionUtils.isNotEmpty(collection) ? collection : null;
  }

  public static <T> List<T> defaultIfEmpty(List<T> collection, List<T> defaultCollection) {
    return CollectionUtils.isNotEmpty(collection) ? collection : defaultCollection;
  }

  public static <T> List<T> filter(Collection<T> collection, Predicate<? super T> predicate) {
    return MyCollectionUtils.filter(collection, predicate, Collectors.toList());
  }

  public static <T, D> List<D> map(Collection<T> collection,
      Function<? super T, ? extends D> mapper) {
    return MyCollectionUtils.map(collection, mapper, Collectors.toList());
  }

  public static <T> List<T> sorted(Collection<T> collection, Comparator<? super T> comparator) {
    return MyCollectionUtils.sorted(collection, comparator, Collectors.toList());
  }

  public static <T> List<T> range(int startInclusive, int endExclusive, IntFunction<T> mapper) {
    return IntStream.range(startInclusive, endExclusive)
        .mapToObj(mapper)
        .collect(Collectors.toList());
  }

  public static <T> List<T> rangeClosed(int startInclusive, int endInclusive,
      IntFunction<T> mapper) {
    return IntStream.rangeClosed(startInclusive, endInclusive)
        .mapToObj(mapper)
        .collect(Collectors.toList());
  }

  public static <T extends Comparable<T>> List<Map.Entry<T, T>> compare(List<T> list1,
      List<T> list2) {
    return IntStream.range(0, CollectionUtils.size(list1))
        .mapToObj(i -> {
          if (CollectionUtils.size(list2) > i) {
            if (list1.get(i).compareTo(list2.get(i)) == 0) {
              return null;
            } else {
              return new AbstractMap.SimpleEntry<>(list1.get(i), list2.get(i));
            }
          } else {
            return new AbstractMap.SimpleEntry<T, T>(list1.get(i), null);
          }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

}
