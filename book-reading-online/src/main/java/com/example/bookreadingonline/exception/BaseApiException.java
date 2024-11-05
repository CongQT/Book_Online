package com.example.bookreadingonline.exception;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true, chain = true)
public class BaseApiException extends RuntimeException {

  private String localizedMessage;

  private List<String> errorCodes = Collections.emptyList();

  private Object extraData = Collections.emptyMap();

  public BaseApiException(Exception e) {
    super(e);
  }

  public BaseApiException(String message) {
    super(message);
  }

  public BaseApiException errorCode(String errorCode) {
    return errorCodes(Collections.singletonList(errorCode));
  }

  public <K, V> BaseApiException extraData(K k1, V v1) {
    return extraData(ImmutableMap.of(k1, v1));
  }

  public <K, V> BaseApiException extraData(K k1, V v1, K k2, V v2) {
    return extraData(ImmutableMap.of(k1, v1, k2, v2));
  }

  public <K, V> BaseApiException extraData(K k1, V v1, K k2, V v2, K k3, V v3) {
    return extraData(ImmutableMap.of(k1, v1, k2, v2, k3, v3));
  }

  public <K, V> BaseApiException extraData(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    return extraData(ImmutableMap.of(k1, v1, k2, v2, k3, v3, k4, v4));
  }

  @Override
  public String getLocalizedMessage() {
    return ObjectUtils.defaultIfNull(localizedMessage, super.getLocalizedMessage());
  }
}
