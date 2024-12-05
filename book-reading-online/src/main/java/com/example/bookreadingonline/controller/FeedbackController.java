package com.example.bookreadingonline.controller;

import com.example.bookreadingonline.payload.request.FeedbackRequest;
import com.example.bookreadingonline.payload.response.FeedbackResponse;
import com.example.bookreadingonline.payload.response.base.BaseResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import com.example.bookreadingonline.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping()
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping("/public/feedback/list")
    public BaseResponse<PageResponse<FeedbackResponse>> list(
            @RequestParam(name = "bookId") Integer bookId,
            Pageable pageable
    ) {
        return BaseResponse.of(feedbackService.list(bookId, pageable));
    }


    @GetMapping("/feedback/get_user_feedback")
    public BaseResponse<FeedbackResponse> getUserFeedback(
            @RequestParam(name = "bookId") Integer bookId) {
        return BaseResponse.of(feedbackService.getUserFeedback(bookId));
    }

    @PostMapping("/feedback/create")
    public BaseResponse<FeedbackResponse> createBook(
            @RequestParam(name = "bookId") Integer bookId,
            @RequestBody @Valid FeedbackRequest request
    ) {
        return BaseResponse.of(feedbackService.createFeedback(bookId, request));
    }

    @PutMapping("/feedback/update")
    public BaseResponse<FeedbackResponse> updateBook(
            @RequestBody @Valid FeedbackRequest request
    ) {
        return BaseResponse.of(feedbackService.updateFeedback(request));
    }

}