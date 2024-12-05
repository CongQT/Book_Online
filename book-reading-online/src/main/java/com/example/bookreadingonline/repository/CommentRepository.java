package com.example.bookreadingonline.repository;

import com.example.bookreadingonline.entity.Author;
import com.example.bookreadingonline.entity.Comment;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;

public interface CommentRepository extends JpaRepositoryImplementation<Comment, Integer> {

    List<Comment> findByParent(Integer parentId);
}