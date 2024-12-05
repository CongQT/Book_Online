package com.example.bookreadingonline.repository;

import com.example.bookreadingonline.entity.Book;
import com.example.bookreadingonline.entity.Chapter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChapterRepository extends JpaRepositoryImplementation<Chapter, Integer> {

    Chapter findByOrderChapAndBookId(Integer id, Integer bookId);

    Chapter findByIdAndBookId(Integer id, Integer bookId);

    List<Chapter> findByBookId(Integer bookId);

    @Query(value = "SELECT * FROM chapter WHERE book_id = :bookId ORDER BY `order_chap` DESC LIMIT 1", nativeQuery = true)
    Chapter findChapterWithHighestOrder(@Param("bookId") Integer bookId);
}