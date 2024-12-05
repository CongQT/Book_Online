package com.example.bookreadingonline.service.impl;

import com.example.bookreadingonline.constant.ErrorCode;
import com.example.bookreadingonline.entity.Comment;
import com.example.bookreadingonline.entity.Like;
import com.example.bookreadingonline.entity.User;
import com.example.bookreadingonline.exception.NotFoundException;
import com.example.bookreadingonline.payload.request.ReactRequest;
import com.example.bookreadingonline.repository.*;
import com.example.bookreadingonline.security.core.userdetails.impl.UserDetailsImpl;
import com.example.bookreadingonline.service.FileService;
import com.example.bookreadingonline.service.LikeService;
import com.example.bookreadingonline.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepo;
    private final CommentRepository commentRepo;

    @Override
    public JpaRepository<Like, Integer> getRepository() {
        return likeRepo;
    }

    @Override
    public void createReact(ReactRequest request, Integer commentId) {
        User user = SecurityUtils.getUserDetails(UserDetailsImpl.class).getUser();
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Not found comment with id: " + commentId)
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", commentId));
        Like likeCheck = likeRepo.findByUserIdAndCommentId(user.getId(), commentId);
        if (likeCheck != null) {
            if (likeCheck.getReact() != null && !request.getLike()) {
                likeCheck.setReact(null);
                comment.setLikeCount(comment.getLikeCount() - 1);
            } else if (likeCheck.getReact() == null && request.getLike()) {
                likeCheck.setReact("LIKE");
                comment.setLikeCount(comment.getLikeCount() + 1);
            }
            likeRepo.save(likeCheck);
        } else {
            Like like = Like.builder()
                    .react("LIKE")
                    .userId(user.getId())
                    .commentId(commentId)
                    .build();
            likeRepo.save(like);
            comment.setLikeCount(comment.getLikeCount() + 1);
        }
        commentRepo.save(comment);
    }
}
