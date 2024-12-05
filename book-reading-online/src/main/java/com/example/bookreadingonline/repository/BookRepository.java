package com.example.bookreadingonline.repository;

import com.example.bookreadingonline.entity.Book;
import com.example.bookreadingonline.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepositoryImplementation<Book, Integer> {

    @Query("SELECT b FROM Book b LEFT JOIN Chapter c ON b.id = c.bookId " +
            "GROUP BY b.id ORDER BY COALESCE(MAX(c.createdAt), '2024-01-01') DESC")
    Page<Book> findAllBooksSortedByChapterOrder(Pageable pageable);
    Page<Book> findByBannerIsNotNull(Pageable pageable);

    List<Book> findByAuthorId(Integer authorId);
}