package com.example.bookreadingonline.service;

import com.example.bookreadingonline.constant.ErrorCode;
import com.example.bookreadingonline.entity.BaseEntity;
import com.example.bookreadingonline.exception.BadRequestException;
import com.example.bookreadingonline.exception.InternalServerErrorException;
import com.example.bookreadingonline.exception.NotFoundException;
import com.example.bookreadingonline.payload.filter.BaseEntityFilter;
import com.example.bookreadingonline.specification.SpecificationBuilder;
import com.example.bookreadingonline.util.Generic;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface BaseEntityService<E extends BaseEntity, ID> extends Generic<E> {

  JpaRepository<E, ID> getRepository();

  default Class<E> getEntityClass() {
    return getClassParam();
  }

  default <D> List<D> findAllAndMap(Function<? super E, ? extends D> mapper) {
    return getRepository().findAll()
            .stream()
            .map(mapper)
            .collect(Collectors.toList());
  }

  default List<E> findAllById(Collection<ID> ids) {
    return CollectionUtils.isNotEmpty(ids) ? getRepository().findAllById(ids)
            : Collections.emptyList();
  }

  default Map<ID, E> findAllByIdToMap(Collection<ID> ids, Function<E, ID> idFunction) {
    return findAllById(ids)
            .stream()
            .collect(Collectors.toMap(idFunction, x -> x));
  }

  default E findById(ID id, Boolean deleted) {
    return getRepository().findById(id)
            .filter(entity -> deleted == null || deleted.equals(entity.isDeleted()))
            .orElseThrow(() -> new NotFoundException("Not found " + getEntityClass().getSimpleName()
                    + " with id: " + id)
                    .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                    .extraData(
                            "entity", getEntityClass().getSimpleName(),
                            "id", id
                    ));
  }

  default E findById(ID id) {
    E entity = findById(id, null);
    if (entity.isDeleted()) {
      throw new BadRequestException("Entity " + getEntityClass().getSimpleName() +
              " with id " + id + " was deleted")
              .errorCode(ErrorCode.ENTITY_WAS_DELETED)
              .extraData(
                      "entity", getEntityClass().getSimpleName(),
                      "id", id
              );
    }
    return entity;
  }

  default E findByNullableId(ID id, Boolean deleted) {
    return Optional.ofNullable(id)
            .map(i -> findById(i, deleted))
            .orElse(null);
  }

  default E findByNullableId(ID id) {
    return Optional.ofNullable(id)
            .map(this::findById)
            .orElse(null);
  }

  default E findByIdOrElse(ID id, E entity) {
    return getRepository().findById(id)
            .orElse(entity);
  }

  default E findByIdOrNull(ID id) {
    return findByIdOrElse(id, null);
  }

  default E findByIdOrGet(ID id, Supplier<? extends E> supplier) {
    return getRepository().findById(id)
            .orElseGet(supplier);
  }

  default <D> void mergeEntities(
          List<E> entities,
          Collection<D> dtos,
          BiPredicate<D, E> checkFunction,
          BiConsumer<D, E> updateFunction,
          Function<D, E> buildFunction
  ) {

    List<E> removedEntities = new ArrayList<>();

    entities.forEach(entity -> {
      D dto = dtos.stream()
              .filter(d -> checkFunction.test(d, entity))
              .findFirst()
              .orElse(null);
      if (dto != null) {
        updateFunction.accept(dto, entity);
      } else {
        removedEntities.add(entity);
      }
    });

    List<E> addedEntities = dtos.stream()
            .filter(dto -> entities.stream()
                    .noneMatch(entity -> checkFunction.test(dto, entity)))
            .map(buildFunction)
            .toList();

    entities.removeAll(removedEntities);
    entities.addAll(addedEntities);

  }

  @SuppressWarnings("unchecked")
  default <F> Page<E> filter(BaseEntityFilter<F> filter) {

    JpaRepository<E, ID> repository = getRepository();
    if (repository instanceof JpaSpecificationExecutor) {
      JpaSpecificationExecutor<E> executor = (JpaSpecificationExecutor<E>) repository;
      Specification<E> specification = SpecificationBuilder.build(filter.getFilters().toArray());
      return executor.findAll(specification, filter.getPageable());
    } else {
      throw new InternalServerErrorException(
              "Repository of entity " + getEntityClass().getSimpleName() +
                      " not support specification filter");
    }

  }

}
