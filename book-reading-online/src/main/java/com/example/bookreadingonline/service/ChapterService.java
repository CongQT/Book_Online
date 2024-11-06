package com.example.bookreadingonline.service;

import com.example.bookreadingonline.entity.Chapter;
import com.example.bookreadingonline.payload.request.ChapterRequest;
import com.example.bookreadingonline.payload.response.BookResponse;
import com.example.bookreadingonline.payload.response.ChapterResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChapterService extends BaseEntityService<Chapter, Integer> {

    ChapterResponse createChapter(Integer bookId, ChapterRequest request);

    ChapterResponse updateChapter(Integer bookId, ChapterRequest request);

    ChapterResponse getChapter(Integer bookId, Integer order);

    void deleteChapter(Integer id);

    PageResponse<ChapterResponse> listSearch(Integer bookId, Pageable pageable);

}