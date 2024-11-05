package com.example.bookreadingonline.controller;

import com.example.bookreadingonline.payload.request.BookRequest;
import com.example.bookreadingonline.payload.response.BookResponse;
import com.example.bookreadingonline.payload.response.base.BaseResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import com.example.bookreadingonline.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/list_search")
    public BaseResponse<PageResponse<BookResponse>> listSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer authorId,
            @RequestParam(required = false) Integer categoryId,
            Pageable pageable
    ) {
        return BaseResponse.of(bookService.listSearch(title, status, authorId, categoryId, pageable));
    }

    @GetMapping("/info/{bookId}")
    public BaseResponse<BookResponse> getEmployeeInfo(
            @PathVariable("bookId") Integer bookId) {
        return BaseResponse.of(bookService.getBook(bookId));
    }

    @PostMapping("/create")
    public BaseResponse<BookResponse> createBook(
            @RequestBody @Valid BookRequest request
    ) {
        return BaseResponse.of(bookService.createBook(request));
    }

    @PutMapping("/update")
    public BaseResponse<BookResponse> updateBook(
            @RequestBody @Valid BookRequest request
    ) {
        return BaseResponse.of(bookService.updateBook(request));
    }

    @GetMapping("/delete/{bookId}")
    public BaseResponse<Object> deleteBook(
            @PathVariable("bookId") Integer bookId) {
        bookService.deleteBook(bookId);
        return BaseResponse.of(Collections.emptyMap());
    }

}