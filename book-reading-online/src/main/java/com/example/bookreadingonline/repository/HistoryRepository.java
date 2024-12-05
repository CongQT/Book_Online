
package com.example.bookreadingonline.repository;

import com.example.bookreadingonline.entity.Feedback;
import com.example.bookreadingonline.entity.History;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;

public interface HistoryRepository extends JpaRepositoryImplementation<History, Integer> {

    List<History> findByUserId(Integer userId);
}