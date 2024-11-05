package com.example.bookreadingonline.service.impl;

import com.example.bookreadingonline.constant.ErrorCode;
import com.example.bookreadingonline.entity.Author;
import com.example.bookreadingonline.entity.Book;
import com.example.bookreadingonline.entity.Category;
import com.example.bookreadingonline.entity.CategoryBook;
import com.example.bookreadingonline.exception.NotFoundException;
import com.example.bookreadingonline.payload.filter.BookFilter;
import com.example.bookreadingonline.payload.filter.BaseEntityFilter;
import com.example.bookreadingonline.payload.filter.CategoryBookFilter;
import com.example.bookreadingonline.payload.request.BookRequest;
import com.example.bookreadingonline.payload.request.CategoryBookRequest;
import com.example.bookreadingonline.payload.response.AuthorResponse;
import com.example.bookreadingonline.payload.response.BookResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import com.example.bookreadingonline.repository.AuthorRepository;
import com.example.bookreadingonline.repository.BookRepository;
import com.example.bookreadingonline.repository.CategoryBookRepository;
import com.example.bookreadingonline.repository.CategoryRepository;
import com.example.bookreadingonline.service.BookService;
import com.example.bookreadingonline.service.FileService;
import com.example.bookreadingonline.util.MyStringUtils;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepo;
    private final CategoryBookRepository categoryBookRepo;
    private final AuthorRepository authorRepo;
    private final CategoryRepository categoryRepo;

    private final FileService fileService;

    private final ModelMapper modelMapper;

    @Override
    public JpaRepository<Book, Integer> getRepository() {
        return bookRepo;
    }

    @Override
    public BookResponse createBook(BookRequest request) {
        List<CategoryBook> categoryBooks = new ArrayList<>();
        Book book = Book.builder()
                .title(request.getTitle())
                .thumbnail(request.getThumbnail())
                .summary(request.getSummary())
                .avgRating(0.0)
                .view(0)
                .status("Chưa hoàn thành")
                .authorId(request.getAuthorId())
                .build();
        bookRepo.save(book);
        request.getCategoryBooks().forEach(categoryBookRequest -> {
            CategoryBook categoryBook = CategoryBook.builder()
                    .categoryId(categoryBookRequest.getCategoryId())
                    .bookId(book.getId())
                    .build();
            categoryBooks.add(categoryBook);
        });
        categoryBookRepo.saveAll(categoryBooks);
        book.setCategoryBooks(categoryBooks);
        return modelMapper.map(book, BookResponse.class);
    }

    @Override
    public BookResponse updateBook(BookRequest request) {
        Book book = bookRepo.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Not found Book with id: " + request.getId())
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", request.getId()));
        modelMapper.map(request, book);
        bookRepo.save(book);
        List<CategoryBook> categoryBooks = new ArrayList<>();
        if (request.getCategoryBooks() != null) {
            if (book.getCategoryBooks() != null) {
                book.getCategoryBooks().forEach(categoryBook -> categoryBookRepo.delete(categoryBook));
            }
            request.getCategoryBooks().forEach(categoryBookRequest -> {
                CategoryBook categoryBook = CategoryBook.builder()
                        .categoryId(categoryBookRequest.getCategoryId())
                        .bookId(book.getId())
                        .build();
                categoryBooks.add(categoryBook);
            });
            categoryBookRepo.saveAll(categoryBooks);
            book.setCategoryBooks(categoryBooks);
        }
        return modelMapper.map(book, BookResponse.class);
    }

    @Override
    @Transactional
    public BookResponse getBook(Integer id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found Book with id: " + id)
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", id));
        BookResponse bookResponse = modelMapper.map(book, BookResponse.class);
        bookResponse.setThumbnailUrl(fileService.getFileUrl(book.getThumbnail()));
        Author author = authorRepo.findById(book.getAuthorId())
                .orElseThrow(() -> new NotFoundException("Not found Author with id: " + book.getAuthorId())
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", book.getAuthorId()));
        bookResponse.setAuthor(modelMapper.map(author, AuthorResponse.class));
        bookResponse.getCategoryBooks().forEach(categoryBookResponse -> {
            Category category = categoryRepo.findById(categoryBookResponse.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Not found Book with id: " + categoryBookResponse.getCategoryId())
                            .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                            .extraData("id", categoryBookResponse.getCategoryId()));
            categoryBookResponse.setCategoryName(category.getName());
        });
        return bookResponse;
    }

    @Override
    public void deleteBook(Integer id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found Book with id: " + id)
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", id));
        bookRepo.delete(book);
    }

    @Override
    @Transactional
    public PageResponse<BookResponse> listSearch(String title, String status, Integer authorId, Integer categoryId, Pageable pageable) {
        BaseEntityFilter<BookFilter> filter = BaseEntityFilter.of(Lists.newArrayList(),
                pageable);
        if (StringUtils.isNotBlank(title) || status != null || authorId != null) {
            BookFilter bookFilter = new BookFilter();
            if (StringUtils.isNotBlank(title)) {
                bookFilter.setTitleLk(MyStringUtils.buildSqlLikePattern(title));
            }
            if (status != null) {
                bookFilter.setStatusLk(status);
            }
            if (authorId != null) {
                bookFilter.setAuthorId(authorId);
            }
            if (categoryId != null) {
                List<CategoryBook> categoryBooks = categoryBookRepo.findByCategoryId(categoryId);
                categoryBooks.forEach(categoryBook -> {
                    bookFilter.setCategoryBooks(CategoryBookFilter.builder()
                            .categoryId(categoryBook.getCategoryId()).build());
                });
            }
            filter.getFilters().add(bookFilter);
        }
        return PageResponse
                .toResponse(filter(filter),
                        book -> {
                            BookResponse bookResponse = modelMapper.map(book, BookResponse.class);
                            bookResponse.setThumbnailUrl(fileService.getFileUrl(book.getThumbnail()));
                            Author author = authorRepo.findById(book.getAuthorId())
                                    .orElseThrow(() -> new NotFoundException("Not found Author with id: " + book.getAuthorId())
                                            .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                                            .extraData("id", book.getAuthorId()));
                            bookResponse.setAuthor(modelMapper.map(author, AuthorResponse.class));
                            bookResponse.getCategoryBooks().forEach(categoryBookResponse -> {
                                Category category = categoryRepo.findById(categoryBookResponse.getCategoryId())
                                        .orElseThrow(() -> new NotFoundException("Not found Book with id: " + categoryBookResponse.getCategoryId())
                                                .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                                                .extraData("id", categoryBookResponse.getCategoryId()));
                                categoryBookResponse.setCategoryName(category.getName());
                            });
                            return bookResponse;
                        });
    }


}
