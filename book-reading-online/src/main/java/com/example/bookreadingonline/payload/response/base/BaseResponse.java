package com.example.bookreadingonline.payload.response.base;

import com.example.bookreadingonline.util.MyLogUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResponse<T> {

  @JsonProperty("data")
  private T data;

  @JsonProperty("request_id")
  private String requestId;

  public static <T> BaseResponse<T> of(T data) {
    return BaseResponse.<T>builder()
        .data(data)
        .requestId(MyLogUtils.getTraceId())
        .build();
  }

}
