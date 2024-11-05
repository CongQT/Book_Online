package com.example.bookreadingonline.payload.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookFilter {

    private String titleLk;
    private String statusLk;
    private Integer authorId;
    private CategoryBookFilter categoryBooks;
}
