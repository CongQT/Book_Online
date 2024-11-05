package com.example.bookreadingonline.service;

import com.example.bookreadingonline.entity.Author;
import com.example.bookreadingonline.entity.Role;
import com.example.bookreadingonline.payload.request.AuthorRequest;
import com.example.bookreadingonline.payload.response.AuthorResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorService extends BaseEntityService<Author, Integer> {

    AuthorResponse createAuthor(AuthorRequest request);

    AuthorResponse updateAuthor(AuthorRequest request);

    AuthorResponse getAuthor(Integer id);

    PageResponse<AuthorResponse> listSearch(String name, Pageable pageable);

}