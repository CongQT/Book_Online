package com.example.bookreadingonline.service;

import com.example.bookreadingonline.entity.Chapter;
import com.example.bookreadingonline.entity.Feedback;
import com.example.bookreadingonline.payload.request.ChapterRequest;
import com.example.bookreadingonline.payload.request.FeedbackRequest;
import com.example.bookreadingonline.payload.response.ChapterResponse;
import com.example.bookreadingonline.payload.response.FeedbackResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import org.springframework.data.domain.Pageable;

public interface FeedbackService extends BaseEntityService<Feedback, Integer> {

    FeedbackResponse createFeedback(Integer bookId, FeedbackRequest request);

    FeedbackResponse updateFeedback(FeedbackRequest request);

    FeedbackResponse getUserFeedback(Integer bookId);

    PageResponse<FeedbackResponse> list(Integer bookId, Pageable pageable);

}