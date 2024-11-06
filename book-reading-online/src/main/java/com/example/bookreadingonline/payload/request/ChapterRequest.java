package com.example.bookreadingonline.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChapterRequest {

    @JsonProperty("id")
    private Integer id;

    @NotBlank
    @JsonProperty("title")
    private String title;

    @NotNull
    @JsonProperty("order_chap")
    private Integer orderChap;

    @NotBlank
    @JsonProperty("file_key")
    private String fileKey;

}