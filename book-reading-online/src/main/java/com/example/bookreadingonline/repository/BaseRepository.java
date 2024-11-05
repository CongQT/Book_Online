package com.example.bookreadingonline.repository;

import com.example.bookreadingonline.constant.ErrorCode;
import com.example.bookreadingonline.entity.BaseEntity;
import com.example.bookreadingonline.exception.NotFoundException;
import com.example.bookreadingonline.specification.SpecificationBuilder;
import com.example.bookreadingonline.util.GenericUtils;
import com.querydsl.core.types.EntityPath;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.querydsl.ListQuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoRepositoryBean
public interface BaseRepository<E extends BaseEntity, T extends EntityPath<E>>
    extends JpaRepositoryImplementation<E, Integer>, ListQuerydslPredicateExecutor<E>,
    QuerydslBinderCustomizer<T> {

  default Class<E> getEntityClass() {
    return GenericUtils.getTypeParameter(getClass());
  }

  default String getEntityName() {
    return getEntityClass().getSimpleName();
  }

  default List<E> findByIds(Collection<Integer> ids) {
    return CollectionUtils.isNotEmpty(ids) ? findAllById(ids) : Collections.emptyList();
  }

  default Map<Integer, E> findByIdsAsMap(Collection<Integer> ids) {
    return findByIdsAsMap(ids, x -> x);
  }

  default <V> Map<Integer, V> findByIdsAsMap(
      Collection<Integer> ids,
      Function<? super E, ? extends V> valueMapper
  ) {
    return findByIdsAsMap(ids, BaseEntity::getId, valueMapper);
  }

  default <K, V> Map<K, V> findByIdsAsMap(
      Collection<Integer> ids,
      Function<? super E, ? extends K> keyMapper,
      Function<? super E, ? extends V> valueMapper
  ) {
    return findByIds(ids)
        .stream()
        .collect(Collectors.toMap(keyMapper, valueMapper));
  }

  default <K> Map<K, List<E>> findByIdsAsGroup(
      Collection<Integer> ids,
      Function<? super E, ? extends K> classifier
  ) {
    return findByIds(ids)
        .stream()
        .collect(Collectors.groupingBy(classifier, Collectors.toList()));
  }

  default <K, V> Map<K, List<V>> findByIdsAsGroup(
      Collection<Integer> ids,
      Function<? super E, ? extends K> classifier,
      Function<? super E, ? extends V> valueMapper
  ) {
    return findByIds(ids)
        .stream()
        .collect(Collectors.groupingBy(classifier,
            Collectors.mapping(valueMapper, Collectors.toList())));
  }

  default Optional<E> findByNullableId(Integer id) {
    return Optional.ofNullable(id).flatMap(this::findById);
  }

  default E get(Integer id) {
    return findByNullableId(id)
        .orElseThrow(() -> new NotFoundException("Not found " + getEntityName() + " with id: " + id)
            .errorCode(ErrorCode.ENTITY_NOT_FOUND));
  }

  default List<E> filter(Object... filters) {
    Specification<E> specification = SpecificationBuilder.build(filters);
    return findAll(specification);
  }

  default List<E> filter(Collection<Object> filters) {
    Specification<E> specification = SpecificationBuilder.build(
        CollectionUtils.emptyIfNull(filters).toArray());
    return findAll(specification);
  }

  @Override
  default void customize(@NonNull QuerydslBindings bindings, @NonNull T root) {
  }

}
