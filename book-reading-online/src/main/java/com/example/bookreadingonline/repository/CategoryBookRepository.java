package com.example.bookreadingonline.repository;

import com.example.bookreadingonline.entity.Category;
import com.example.bookreadingonline.entity.CategoryBook;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;

public interface CategoryBookRepository extends JpaRepositoryImplementation<CategoryBook, Integer> {

    List<CategoryBook> findByCategoryId(Integer categoryId);
}