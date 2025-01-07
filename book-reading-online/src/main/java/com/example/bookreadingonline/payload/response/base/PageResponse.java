package com.example.bookreadingonline.payload.response.base;

import com.example.bookreadingonline.util.MyListUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageResponse<T> {

  @JsonProperty("content")
  private List<T> content;

  @JsonProperty("number")
  private int number;

  @JsonProperty("size")
  private int size;

  @JsonProperty("number_of_elements")
  private int numberOfElements;

  @JsonProperty("is_first")
  private boolean first;

  @JsonProperty("is_last")
  private boolean last;

  @JsonProperty("total_pages")
  private int totalPages;

  @JsonProperty("total_elements")
  private long totalElements;

  public static <T> PageResponse<T> toResponse(Page<T> page) {
    return PageResponse.<T>builder()
        .content(page.getContent())
        .number(page.getNumber() + 1)
        .size(page.getSize())
        .numberOfElements(page.getNumberOfElements())
        .first(page.isFirst())
        .last(page.isLast())
        .totalPages(page.getTotalPages())
        .totalElements(page.getTotalElements())
        .build();
  }

  public static <R, T> PageResponse<T> toResponse(Page<R> page, Function<R, T> function) {
    return toResponse(page.map(function));
  }

}