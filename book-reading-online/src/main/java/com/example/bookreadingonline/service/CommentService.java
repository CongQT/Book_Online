package com.example.bookreadingonline.service;

import com.example.bookreadingonline.entity.Comment;
import com.example.bookreadingonline.entity.Comment;
import com.example.bookreadingonline.payload.request.CommentRequest;
import com.example.bookreadingonline.payload.response.CommentReplyResponse;
import com.example.bookreadingonline.payload.response.CommentResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CommentService extends BaseEntityService<Comment, Integer> {

    CommentResponse createComment(CommentRequest request);

    CommentReplyResponse createCommentReply(CommentRequest request);

    CommentResponse updateComment(CommentRequest request);

    PageResponse<CommentResponse> listComment(Integer chapterId, Pageable pageable);

    PageResponse<CommentReplyResponse> listCommentReply(Integer parentId, Pageable pageable);

    void deleteComment(Integer id);

}