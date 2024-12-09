package com.example.bookreadingonline.controller;

import com.example.bookreadingonline.payload.request.RegistrationRequest;
import com.example.bookreadingonline.payload.response.BookHistoryResponse;
import com.example.bookreadingonline.payload.response.base.BaseResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import com.example.bookreadingonline.service.BookService;
import com.example.bookreadingonline.service.HistoryService;
import com.example.bookreadingonline.service.SignupService;
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
public class HistoryController {
    private final HistoryService historyService;
    private final BookService bookService;

    @PostMapping("/history")
    public BaseResponse<Object> saveHistory(@RequestParam(name = "chapterId") Integer chapterId) {
        historyService.saveHistory(chapterId);
        return BaseResponse.of(Collections.emptyMap());
    }

    @PostMapping("/public/update_view")
    public BaseResponse<Object> updateView(@RequestParam(name = "chapterId") Integer chapterId) {
        historyService.updateView(chapterId);
        return BaseResponse.of(Collections.emptyMap());
    }

    @GetMapping("/book/list_history")
    public BaseResponse<PageResponse<BookHistoryResponse>> listSearch(
            Pageable pageable
    ) {
        return BaseResponse.of(bookService.list(pageable));
    }

}
