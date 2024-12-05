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

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping()
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/public/author/list_search")
    public BaseResponse<PageResponse<AuthorResponse>> listSearch(
            @RequestParam(required = false) String name,
            Pageable pageable
    ) {
        return BaseResponse.of(authorService.listSearch(name, pageable));
    }

    @GetMapping("/public/author/info/{authorId}")
    public BaseResponse<AuthorResponse> getAuthorInfo(
            @PathVariable("authorId") Integer authorId)
    {
        return BaseResponse.of(authorService.getAuthor(authorId));
    }

    @PostMapping("/author/create")
    public BaseResponse<AuthorResponse> createAuthor(
            @RequestBody @Valid AuthorRequest request
    ) {
        return BaseResponse.of(authorService.createAuthor(request));
    }

    @PutMapping("/author/update")
    public BaseResponse<AuthorResponse> updateAuthor(
            @RequestBody @Valid AuthorRequest request
    ) {
        return BaseResponse.of(authorService.updateAuthor(request));
    }

    @DeleteMapping("/author/delete/{authorId}")
    public BaseResponse<Object> deleteAuthor(
            @PathVariable("authorId") Integer authorId) {
        authorService.deleteAuthor(authorId);
        return BaseResponse.of(Collections.emptyMap());
    }

}