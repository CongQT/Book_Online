package com.example.bookreadingonline.service;

import com.example.bookreadingonline.entity.Comment;
import com.example.bookreadingonline.entity.Like;
import com.example.bookreadingonline.payload.request.ReactRequest;

public interface LikeService extends BaseEntityService<Like, Integer> {

    void createReact(ReactRequest request, Integer commentId);
}
