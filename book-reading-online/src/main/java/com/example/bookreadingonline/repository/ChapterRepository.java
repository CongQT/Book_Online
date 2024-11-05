package com.example.bookreadingonline.repository;

import com.example.bookreadingonline.entity.Book;
import com.example.bookreadingonline.entity.Chapter;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

public interface ChapterRepository extends JpaRepositoryImplementation<Chapter, Integer> {

}