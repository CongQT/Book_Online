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
import com.example.bookreadingonline.service.AuthorService;
import com.example.bookreadingonline.util.MyStringUtils;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepo;

    private final ModelMapper modelMapper;

    @Override
    public JpaRepository<Author, Integer> getRepository() {
        return authorRepo;
    }

    @Override
    public AuthorResponse createAuthor(AuthorRequest request) {
        Author author = Author.builder()
                .name(request.getName())
                .build();
        authorRepo.save(author);
        return modelMapper.map(author, AuthorResponse.class);
    }

    @Override
    public AuthorResponse updateAuthor(AuthorRequest request) {
        Author author = authorRepo.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Not found Author with id: " + request.getId())
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", request.getId()));
        modelMapper.map(request, author);
        authorRepo.save(author);
        return modelMapper.map(author, AuthorResponse.class);
    }

    @Override
    public AuthorResponse getAuthor(Integer id) {
        Author author = authorRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found Author with id: " + id)
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", id));
        return modelMapper.map(author, AuthorResponse.class);
    }

    @Override
    public void deleteAuthor(Integer id) {
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
                .toResponse(filter(filter), author -> modelMapper.map(author, AuthorResponse.class));
    }


}
