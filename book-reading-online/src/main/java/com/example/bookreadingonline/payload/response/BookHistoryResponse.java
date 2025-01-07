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

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookHistoryResponse {

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("title")
  private String title;

  @JsonProperty("chapter_id")
  private Integer chapterId;

  @JsonProperty("chapter_history")
  private ChapterHistoryResponse chapter;

  @JsonProperty("summary")
  private String summary;

  @JsonIgnore
  private Double avgRating;

  @JsonGetter("avg_rating")
  public Double getAvgRating() {
    return Math.round(avgRating * 100.0) / 100.0;
  }


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