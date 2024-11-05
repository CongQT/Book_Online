package com.example.bookreadingonline.repository;

import com.example.bookreadingonline.entity.Book;
import com.example.bookreadingonline.entity.Category;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

public interface BookRepository extends JpaRepositoryImplementation<Book, Integer> {

}