package com.example.bookreadingonline.payload.response;

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
public class CommentResponse {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("chapter_id")
    private Integer chapterId;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("title")
    private String title;

    @JsonProperty("like_count")
    private Integer likeCount;

    @JsonProperty("reply_count")
    private Integer replyCount;

    @JsonProperty("last_updated")
    private String lastUpdated;

    @JsonProperty("check_like")
    private Boolean checkLike;

    @JsonProperty("user")
    private UserInfoResponse user;

}