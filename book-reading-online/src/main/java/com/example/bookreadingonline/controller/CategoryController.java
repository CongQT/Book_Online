package com.example.bookreadingonline.controller;

import com.example.bookreadingonline.payload.request.CategoryRequest;
import com.example.bookreadingonline.payload.response.CategoryResponse;
import com.example.bookreadingonline.payload.response.base.BaseResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import com.example.bookreadingonline.service.CategoryService;
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
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/public/category/list_search")
    public BaseResponse<PageResponse<CategoryResponse>> listSearch(
            @RequestParam(required = false) String name,
            Pageable pageable
    ) {
        return BaseResponse.of(categoryService.listSearch(name, pageable));
    }

    @GetMapping("/public/category/info/{categoryId}")
    public BaseResponse<CategoryResponse> getCategoryInfo(
            @PathVariable("categoryId") Integer categoryId)
    {
        return BaseResponse.of(categoryService.getCategory(categoryId));
    }

    @PostMapping("/category/create")
    public BaseResponse<CategoryResponse> createCategory(
            @RequestBody @Valid CategoryRequest request
    ) {
        return BaseResponse.of(categoryService.createCategory(request));
    }

    @PutMapping("/category/update")
    public BaseResponse<CategoryResponse> updateCategory(
            @RequestBody @Valid CategoryRequest request
    ) {
        return BaseResponse.of(categoryService.updateCategory(request));
    }

    @GetMapping("/category/delete/{categoryId}")
    public BaseResponse<Object> deleteCategory(
            @PathVariable("categoryId") Integer categoryId) {
        categoryService.deleteCategory(categoryId);
        return BaseResponse.of(Collections.emptyMap());
    }

}