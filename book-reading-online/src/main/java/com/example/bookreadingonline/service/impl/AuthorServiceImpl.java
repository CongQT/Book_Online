package com.example.bookreadingonline.service.impl;

import com.example.bookreadingonline.constant.ErrorCode;
import com.example.bookreadingonline.entity.Author;
import com.example.bookreadingonline.entity.Book;
import com.example.bookreadingonline.exception.NotFoundException;
import com.example.bookreadingonline.payload.filter.AuthorFilter;
import com.example.bookreadingonline.payload.filter.BaseEntityFilter;
import com.example.bookreadingonline.payload.request.AuthorRequest;
import com.example.bookreadingonline.payload.response.AuthorResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import com.example.bookreadingonline.repository.AuthorRepository;
import com.example.bookreadingonline.repository.BookRepository;
import com.example.bookreadingonline.service.AuthorService;
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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepo;
    private final BookRepository bookRepo;

    private final FileService fileService;

    private final ModelMapper modelMapper;

    @Override
    public JpaRepository<Author, Integer> getRepository() {
        return authorRepo;
    }

    @Override
    public AuthorResponse createAuthor(AuthorRequest request) {
        Author author = Author.builder()
                .name(request.getName())
                .description(request.getDescription())
                .image(request.getImage())
                .build();
        authorRepo.save(author);
        AuthorResponse authorResponse = modelMapper.map(author, AuthorResponse.class);
        if (author.getImage() != null) authorResponse.setImageUrl(fileService.getFileUrl(authorResponse.getImage()));
        return authorResponse;
    }

    @Override
    public AuthorResponse updateAuthor(AuthorRequest request) {
        Author author = authorRepo.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Not found Author with id: " + request.getId())
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", request.getId()));
        modelMapper.map(request, author);
        authorRepo.save(author);
        AuthorResponse authorResponse = modelMapper.map(author, AuthorResponse.class);
        if (author.getImage() != null) authorResponse.setImageUrl(fileService.getFileUrl(authorResponse.getImage()));
        return authorResponse;
    }

    @Override
    public AuthorResponse getAuthor(Integer id) {
        Author author = authorRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found Author with id: " + id)
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", id));

        AuthorResponse authorResponse = modelMapper.map(author, AuthorResponse.class);
        if (author.getImage() != null) authorResponse.setImageUrl(fileService.getFileUrl(authorResponse.getImage()));
        return authorResponse;
    }

    @Override
    public void deleteAuthor(Integer id) {
        List<Book> books = bookRepo.findByAuthorId(id);
        if(books != null){
            books.forEach(book -> {
                book.setAuthorId(1);
            });
            bookRepo.saveAll(books);
        }
        Author author = authorRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found Author with id: " + id)
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", id));
        authorRepo.delete(author);
        
    }

    @Override
    public PageResponse<AuthorResponse> listSearch(String name, Pageable pageable) {
        BaseEntityFilter<AuthorFilter> filter = BaseEntityFilter.of(Lists.newArrayList(), pageable);
        if (StringUtils.isNotBlank(name)) {
            filter.getFilters().add(AuthorFilter.builder()
                    .nameLk(MyStringUtils.buildSqlLikePattern(name))
                    .build());
        }
        return PageResponse
                .toResponse(filter(filter), author -> {
                    AuthorResponse authorResponse = modelMapper.map(author, AuthorResponse.class);
                    if (author.getImage() != null)
                        authorResponse.setImageUrl(fileService.getFileUrl(authorResponse.getImage()));
                    return authorResponse;
                });
    }


}
