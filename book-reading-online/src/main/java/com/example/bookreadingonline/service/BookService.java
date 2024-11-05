package com.example.bookreadingonline.service;

import com.example.bookreadingonline.entity.Book;
import com.example.bookreadingonline.payload.request.BookRequest;
import com.example.bookreadingonline.payload.response.BookResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import org.springframework.data.domain.Pageable;

public interface BookService extends BaseEntityService<Book, Integer> {

    BookResponse createBook(BookRequest request);

    BookResponse updateBook(BookRequest request);

    BookResponse getBook(Integer id);

    void deleteBook(Integer id);

    PageResponse<BookResponse> listSearch(String title, String status, Integer authorId, Integer categoryId, Pageable pageable);

}