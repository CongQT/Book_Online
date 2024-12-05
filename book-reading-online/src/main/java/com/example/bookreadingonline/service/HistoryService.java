package com.example.bookreadingonline.service;

import com.example.bookreadingonline.entity.Feedback;
import com.example.bookreadingonline.entity.History;
import com.example.bookreadingonline.payload.request.FeedbackRequest;
import com.example.bookreadingonline.payload.response.FeedbackResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import org.springframework.data.domain.Pageable;

public interface HistoryService extends BaseEntityService<History, Integer> {

    void saveHistory(Integer chapterId);

    void updateView(Integer chapterId);

}