package com.example.bookreadingonline.controller;

import com.example.bookreadingonline.payload.request.AuthorRequest;
import com.example.bookreadingonline.payload.response.AuthorResponse;
import com.example.bookreadingonline.payload.response.base.BaseResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import com.example.bookreadingonline.service.AuthorService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/author")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/list_search")
    public BaseResponse<PageResponse<AuthorResponse>> listSearch(
            @RequestParam(required = false) String name,
            Pageable pageable
    ) {
        return BaseResponse.of(authorService.listSearch(name, pageable));
    }

    @GetMapping("/info/{authorId}")
    public BaseResponse<AuthorResponse> getEmployeeInfo(
            @PathVariable("authorId") Integer authorId)
    {
        return BaseResponse.of(authorService.getAuthor(authorId));
    }

    @PostMapping("/create")
    public BaseResponse<AuthorResponse> createAuthor(
            @RequestBody @Valid AuthorRequest request
    ) {
        return BaseResponse.of(authorService.createAuthor(request));
    }

    @PutMapping("/update")
    public BaseResponse<AuthorResponse> updateAuthor(
            @RequestBody @Valid AuthorRequest request
    ) {
        return BaseResponse.of(authorService.updateAuthor(request));
    }

}