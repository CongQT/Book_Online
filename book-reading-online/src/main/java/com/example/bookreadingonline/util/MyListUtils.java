package com.example.bookreadingonline.util;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MyListUtils {

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

}
