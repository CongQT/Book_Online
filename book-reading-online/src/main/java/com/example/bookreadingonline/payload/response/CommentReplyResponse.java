package com.example.bookreadingonline.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentReplyResponse {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("title")
    private String title;

    @JsonProperty("like_count")
    private Integer likeCount;

    @JsonProperty("parent_id")
    private Integer parent;

    @JsonProperty("reply_name")
    private String replyName;

    @JsonProperty("last_updated")
    private String lastUpdated;

    @JsonProperty("check_like")
    private Boolean checkLike;

    @JsonProperty("user")
    private UserInfoResponse user;

}