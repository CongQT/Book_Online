
package com.example.bookreadingonline.repository;

import com.example.bookreadingonline.entity.Chapter;
import com.example.bookreadingonline.entity.Feedback;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;

public interface FeedbackRepository extends JpaRepositoryImplementation<Feedback, Integer> {

    Feedback findByUserIdAndBookId(Integer userId, Integer bookId);

    List<Feedback> findByBookId(Integer bookId);
}