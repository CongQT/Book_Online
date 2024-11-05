package com.example.bookreadingonline.repository;

import com.example.bookreadingonline.entity.Author;
import com.example.bookreadingonline.entity.Category;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

public interface CategoryRepository extends JpaRepositoryImplementation<Category, Integer> {

}