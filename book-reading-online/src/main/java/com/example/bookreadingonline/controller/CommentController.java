package com.example.bookreadingonline.controller;

import com.example.bookreadingonline.payload.request.CommentRequest;
import com.example.bookreadingonline.payload.response.CommentReplyResponse;
import com.example.bookreadingonline.payload.response.CommentResponse;
import com.example.bookreadingonline.payload.response.base.BaseResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import com.example.bookreadingonline.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping()
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/public/comment/list")
    public BaseResponse<PageResponse<CommentResponse>> list(
            @RequestParam(name = "chapterId") Integer chapterId,
            Pageable pageable
    ) {
        return BaseResponse.of(commentService.listComment(chapterId, pageable));
    }


    @GetMapping("/public/comment/list_reply")
    public BaseResponse<PageResponse<CommentReplyResponse>> listReply(
            @RequestParam(name = "parentId") Integer parentId,
            Pageable pageable) {
        return BaseResponse.of(commentService.listCommentReply(parentId, pageable));
    }

    @PostMapping("/comment/create")
    public BaseResponse<CommentResponse> create(
            @RequestBody @Valid CommentRequest request
    ) {
        return BaseResponse.of(commentService.createComment(request));
    }

    @PostMapping("/comment/create_reply")
    public BaseResponse<CommentReplyResponse> createReply(
            @RequestBody @Valid CommentRequest request
    ) {
        return BaseResponse.of(commentService.createCommentReply(request));
    }

    @PutMapping("/comment/update")
    public BaseResponse<CommentResponse> update(
            @RequestBody @Valid CommentRequest request
    ) {
        return BaseResponse.of(commentService.updateComment(request));
    }

    @DeleteMapping("/comment/delete")
    public BaseResponse<Object> deleteComment(
        @RequestParam(name = "id") Integer id) {
        commentService.deleteComment(id);
        return BaseResponse.of(Collections.emptyMap());
    }

}