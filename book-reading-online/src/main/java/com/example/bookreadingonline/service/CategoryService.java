package com.example.bookreadingonline.service;

import com.example.bookreadingonline.entity.Category;
import com.example.bookreadingonline.payload.request.CategoryRequest;
import com.example.bookreadingonline.payload.response.CategoryResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryService extends BaseEntityService<Category, Integer> {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(CategoryRequest request);

    CategoryResponse getCategory(Integer id);

    PageResponse<CategoryResponse> listSearch(String name, Pageable pageable);

}