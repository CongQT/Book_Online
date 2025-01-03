package com.example.bookreadingonline.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChapterResponse {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("order_next")
    private Integer orderNext;

    @JsonProperty("order_previous")
    private Integer orderPrevious;

    @JsonProperty("title")
    private String title;

    @JsonProperty("order_chap")
    private Integer orderChap;

    @JsonProperty("book")
    private BookResponse book;

    @JsonProperty("file_key")
    private String fileKey;

    @JsonProperty("file_url")
    private String fileUrl;

}