package com.example.bookreadingonline.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookResponse {

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("title")
  private String title;

  @JsonProperty("summary")
  private String summary;

  @JsonProperty("avg_rating")
  private Double avgRating;

  @JsonProperty("thumbnail_url")
  private String thumbnailUrl;

  @JsonProperty("view")
  private Integer view;

  @JsonProperty("status")
  private String status;

  @JsonProperty("created_at")
  private LocalDateTime createdAt;

  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;

  @JsonProperty("author")
  private AuthorResponse author;

  @JsonProperty("category_book")
  private List<CategoryBookResponse> categoryBooks;

}