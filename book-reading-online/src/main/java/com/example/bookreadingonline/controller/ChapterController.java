package com.example.bookreadingonline.controller;

import com.example.bookreadingonline.payload.request.ChapterRequest;
import com.example.bookreadingonline.payload.response.AuthorResponse;
import com.example.bookreadingonline.payload.response.ChapterResponse;
import com.example.bookreadingonline.payload.response.base.BaseResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import com.example.bookreadingonline.service.BookService;
import com.example.bookreadingonline.service.ChapterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping()
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @GetMapping("/public/chapter/list")
    public BaseResponse<PageResponse<ChapterResponse>> list(
            @RequestParam(name = "bookId") Integer bookId,
            Pageable pageable
    ) {
        return BaseResponse.of(chapterService.listSearch(bookId, pageable));
    }


    @GetMapping("/public/chapter/get")
    public BaseResponse<ChapterResponse> getEmployeeInfo(
            @RequestParam(name = "bookId") Integer bookId,
            @RequestParam(name = "order") Integer order) {
        return BaseResponse.of(chapterService.getChapter(bookId, order));
    }

    @PostMapping("/chapter/create")
    public BaseResponse<ChapterResponse> createBook(
            @RequestParam(name = "bookId") Integer bookId,
            @RequestBody @Valid ChapterRequest request
    ) {
        return BaseResponse.of(chapterService.createChapter(bookId, request));
    }

    @PutMapping("/chapter/update")
    public BaseResponse<ChapterResponse> updateBook(
            @RequestParam(name = "bookId") Integer bookId,
            @RequestBody @Valid ChapterRequest request
    ) {
        return BaseResponse.of(chapterService.updateChapter(bookId, request));
    }

    @DeleteMapping("/chapter/delete/{chapterId}")
    public BaseResponse<Object> deleteBook(
            @PathVariable("chapterId") Integer chapterId) {
        chapterService.deleteChapter(chapterId);
        return BaseResponse.of(Collections.emptyMap());
    }

}