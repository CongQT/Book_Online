package com.example.bookreadingonline.payload.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

  @JsonIgnore
  private Double avgRating;

  @JsonGetter("avg_rating")
  public Double getAvgRating() {
    return Math.round(avgRating * 10.0) / 10.0;
  }

  @JsonProperty("thumbnail_key")
  private String thumbnail;

  @JsonProperty("banner_key")
  private String banner;

  @JsonProperty("thumbnail_url")
  private String thumbnailUrl;

  @JsonProperty("banner_url")
  private String bannerUrl;

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

  @JsonProperty("new_chapter")
  private NewChapterResponse newChapter;


}