package com.example.bookreadingonline.repository;

import com.example.bookreadingonline.entity.Book;
import com.example.bookreadingonline.entity.Chapter;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;

public interface ChapterRepository extends JpaRepositoryImplementation<Chapter, Integer> {

    Chapter findByOrderChapAndBookId(Integer id, Integer bookId);

    Chapter findByIdAndBookId(Integer id, Integer bookId);

    List<Chapter> findByBookId(Integer bookId);
}