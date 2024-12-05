package com.example.bookreadingonline.service.impl;

import com.example.bookreadingonline.constant.ErrorCode;
import com.example.bookreadingonline.entity.Author;
import com.example.bookreadingonline.entity.Category;
import com.example.bookreadingonline.entity.CategoryBook;
import com.example.bookreadingonline.exception.NotFoundException;
import com.example.bookreadingonline.payload.filter.CategoryFilter;
import com.example.bookreadingonline.payload.filter.BaseEntityFilter;
import com.example.bookreadingonline.payload.request.CategoryRequest;
import com.example.bookreadingonline.payload.response.CategoryResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import com.example.bookreadingonline.repository.CategoryBookRepository;
import com.example.bookreadingonline.repository.CategoryRepository;
import com.example.bookreadingonline.service.CategoryService;
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
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepo;
    private final CategoryBookRepository categoryBookRepo;

    private final ModelMapper modelMapper;

    @Override
    public JpaRepository<Category, Integer> getRepository() {
        return categoryRepo;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .build();
        categoryRepo.save(category);
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    public CategoryResponse updateCategory(CategoryRequest request) {
        Category category = categoryRepo.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Not found Category with id: " + request.getId())
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", request.getId()));
        modelMapper.map(request, category);
        categoryRepo.save(category);
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    public CategoryResponse getCategory(Integer id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found Category with id: " + id)
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", id));
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    public void deleteCategory(Integer id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found Category with id: " + id)
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", id));
        categoryRepo.delete(category);

        List<CategoryBook> categoryBookList = categoryBookRepo.findByCategoryId(id);
        if(categoryBookList != null) categoryBookRepo.deleteAll(categoryBookList);

    }

    @Override
    public PageResponse<CategoryResponse> listSearch(String name, Pageable pageable) {
        BaseEntityFilter<CategoryFilter> filter = BaseEntityFilter.of(Lists.newArrayList(), pageable);
        if (StringUtils.isNotBlank(name)) {
            filter.getFilters().add(CategoryFilter.builder()
                    .nameLk(MyStringUtils.buildSqlLikePattern(name))
                    .build());
        }
        return PageResponse
                .toResponse(filter(filter), category -> modelMapper.map(category, CategoryResponse.class));
    }


}
