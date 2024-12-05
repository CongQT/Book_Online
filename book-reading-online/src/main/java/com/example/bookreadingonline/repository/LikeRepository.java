package com.example.bookreadingonline.repository;

import com.example.bookreadingonline.entity.Comment;
import com.example.bookreadingonline.entity.Like;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;

public interface LikeRepository extends JpaRepositoryImplementation<Like, Integer> {

    Like findByUserIdAndCommentId(Integer userId, Integer commentId);

    List<Like> findByCommentId(Integer id);
}