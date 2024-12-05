package com.example.bookreadingonline.service.impl;

import com.example.bookreadingonline.constant.ErrorCode;
import com.example.bookreadingonline.entity.Chapter;
import com.example.bookreadingonline.entity.Comment;
import com.example.bookreadingonline.entity.Like;
import com.example.bookreadingonline.entity.User;
import com.example.bookreadingonline.exception.BadRequestException;
import com.example.bookreadingonline.exception.NotFoundException;
import com.example.bookreadingonline.payload.filter.BaseEntityFilter;
import com.example.bookreadingonline.payload.filter.ChapterFilter;
import com.example.bookreadingonline.payload.filter.CommentFilter;
import com.example.bookreadingonline.payload.filter.FeedbackFilter;
import com.example.bookreadingonline.payload.request.CommentRequest;
import com.example.bookreadingonline.payload.response.CommentReplyResponse;
import com.example.bookreadingonline.payload.response.CommentResponse;
import com.example.bookreadingonline.payload.response.FeedbackResponse;
import com.example.bookreadingonline.payload.response.UserInfoResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import com.example.bookreadingonline.repository.*;
import com.example.bookreadingonline.security.core.userdetails.impl.UserDetailsImpl;
import com.example.bookreadingonline.service.CommentService;
import com.example.bookreadingonline.service.FileService;
import com.example.bookreadingonline.util.SecurityUtils;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepo;
    private final ChapterRepository chapterRepo;
    private final LikeRepository likeRepo;
    private final UserRepository userRepo;

    private final FileService fileService;

    private final ModelMapper modelMapper;

    @Override
    public JpaRepository<Comment, Integer> getRepository() {
        return commentRepo;
    }

    @Override
    @Transactional
    public CommentResponse createComment(CommentRequest request) {
        User user = SecurityUtils.getUserDetails(UserDetailsImpl.class).getUser();
        Chapter chapter = chapterRepo.findById(request.getChapterId())
                .orElseThrow(() -> new NotFoundException("Not found chap with id: " + request.getChapterId())
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", request.getChapterId()));
        Comment comment = Comment.builder()
                .comment(request.getComment())
                .title(chapter.getTitle())
                .likeCount(0)
                .replyCount(0)
                .parent(-1)
                .chapterId(request.getChapterId())
                .userId(user.getId())
                .bookId(chapter.getBookId())
                .build();
        commentRepo.save(comment);
        return modelMapper.map(comment, CommentResponse.class);
    }

    @Override
    @Transactional
    public CommentReplyResponse createCommentReply(CommentRequest request) {
        User user = SecurityUtils.getUserDetails(UserDetailsImpl.class).getUser();
        Comment comment = commentRepo.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Not found comment with id: " + request.getId())
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", request.getId()));
        Chapter chapter = chapterRepo.findById(comment.getChapterId())
                .orElseThrow(() -> new NotFoundException("Not found chap with id: " + comment.getChapterId())
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", comment.getChapterId()));
        User user1 = userRepo.findById(comment.getUserId())
                .orElseThrow(() -> new NotFoundException("Not found user with id: " + comment.getUserId())
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", comment.getUserId()));
        Comment commentReply = Comment.builder()
                .comment(request.getComment())
                .title(chapter.getTitle())
                .likeCount(0)
                .replyCount(0)
                .parent(request.getId())
                .replyName(user1.getUsername())
                .chapterId(comment.getChapterId())
                .userId(user.getId())
                .bookId(chapter.getBookId())
                .build();
        commentRepo.save(commentReply);
        comment.setReplyCount(comment.getReplyCount() + 1);
        commentRepo.save(comment);
        return modelMapper.map(commentReply, CommentReplyResponse.class);
    }

    @Override
    public CommentResponse updateComment(CommentRequest request) {
        Comment comment = commentRepo.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Not found comment with id: " + request.getId())
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", request.getId()));
        modelMapper.map(request, comment);
        commentRepo.save(comment);
        return modelMapper.map(comment, CommentResponse.class);
    }

    @Override
    public PageResponse<CommentResponse> listComment(Integer chapterId, Pageable pageable) {
        Object check = SecurityUtils.getUserDetails(UserDetailsImpl.class);
        List<Comment> filteredComments = commentRepo.findAll().stream()
                .filter(comment -> comment.getParent() == null)
                .collect(Collectors.toList());
        BaseEntityFilter<CommentFilter> filter = BaseEntityFilter.of(Lists.newArrayList(), pageable);
        filter.getFilters().add(CommentFilter.builder()
                .chapterId(chapterId)
                .parent(-1)
                .build());
        return PageResponse
                .toResponse(filter(filter), comment -> {
                    User user = userRepo.findById(comment.getUserId())
                            .orElseThrow(() -> new NotFoundException("Not found User with id: " + comment.getUserId())
                                    .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                                    .extraData("id", comment.getUserId()));
                    CommentResponse commentResponse = modelMapper.map(comment, CommentResponse.class);
                    UserInfoResponse userInfoResponse = modelMapper.map(user, UserInfoResponse.class);
                    userInfoResponse.setAvatar_url(fileService.getFileUrl(user.getAvatar()));
                    commentResponse.setUser(userInfoResponse);
                    String updateTimeString = comment.getCreatedAt().toString();
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                    LocalDateTime updateTime = LocalDateTime.parse(updateTimeString, formatter);
                    LocalDateTime now = LocalDateTime.now();
                    commentResponse.setLastUpdated(roundTime(updateTime, now));
                    if (check != null) {
                        User userLogin = SecurityUtils.getUserDetails(UserDetailsImpl.class).getUser();
                        Like like = likeRepo.findByUserIdAndCommentId(userLogin.getId(), comment.getId());
                        if (like != null) {
                            if (like.getReact() != null) commentResponse.setCheckLike(true);
                            else commentResponse.setCheckLike(false);
                        } else commentResponse.setCheckLike(false);
                    }
                    return commentResponse;
                });
    }

    @Override
    public PageResponse<CommentReplyResponse> listCommentReply(Integer parentId, Pageable pageable) {
        Object check = SecurityUtils.getUserDetails(UserDetailsImpl.class);
        BaseEntityFilter<CommentFilter> filter = BaseEntityFilter.of(Lists.newArrayList(), pageable);
        filter.getFilters().add(CommentFilter.builder()
                .parent(parentId)
                .build());
        return PageResponse
                .toResponse(filter(filter), comment -> {
                    User user = userRepo.findById(comment.getUserId())
                            .orElseThrow(() -> new NotFoundException("Not found User with id: " + comment.getUserId())
                                    .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                                    .extraData("id", comment.getUserId()));
                    CommentReplyResponse commentResponse = modelMapper.map(comment, CommentReplyResponse.class);
                    UserInfoResponse userInfoResponse = modelMapper.map(user, UserInfoResponse.class);
                    userInfoResponse.setAvatar_url(fileService.getFileUrl(user.getAvatar()));
                    commentResponse.setUser(userInfoResponse);
                    String updateTimeString = comment.getCreatedAt().toString();
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                    LocalDateTime updateTime = LocalDateTime.parse(updateTimeString, formatter);
                    LocalDateTime now = LocalDateTime.now();
                    commentResponse.setLastUpdated(roundTime(updateTime, now));
                    if (check != null) {
                        User userLogin = SecurityUtils.getUserDetails(UserDetailsImpl.class).getUser();
                        Like like = likeRepo.findByUserIdAndCommentId(userLogin.getId(), comment.getId());
                        if (like != null) {
                            if (like.getReact() != null) commentResponse.setCheckLike(true);
                            else commentResponse.setCheckLike(false);
                        } else commentResponse.setCheckLike(false);
                    }
                    return commentResponse;
                });
    }

    private String roundTime(LocalDateTime updateTime, LocalDateTime now) {
        long days = ChronoUnit.DAYS.between(updateTime, now);
        long hours = ChronoUnit.HOURS.between(updateTime, now) % 24;
        long minutes = ChronoUnit.MINUTES.between(updateTime, now) % 60;
        long seconds = ChronoUnit.SECONDS.between(updateTime, now) % 60;
        long weeks = days / 7;
        long months = days / 30;

        StringBuilder timeString = new StringBuilder();
        if (months > 0) {
            timeString.append(months).append(" tháng trước");
        } else if (weeks > 0) {
            timeString.append(weeks).append(" tuần trước");
        } else if (days > 0) {
            timeString.append(days).append(" ngày trước");
        } else if (hours > 0) {
            timeString.append(hours).append(" giờ trước");
        } else if (minutes > 0) {
            timeString.append(minutes).append(" phút trước");
        } else {
            timeString.append(seconds).append(" giây trước");
        }
        return timeString.toString();
    }

    @Override
    public void deleteComment(Integer id) {
        User user = SecurityUtils.getUserDetails(UserDetailsImpl.class).getUser();
        List<Like> likes = likeRepo.findByCommentId(id);
        likeRepo.deleteAll(likes);
        Comment comment = commentRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found comment with id: " + id)
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", id));
        if (comment.getParent() != null) {
            Comment comment1 = commentRepo.findById(comment.getParent())
                    .orElseThrow(() -> new NotFoundException("Not found comment with id: " + comment.getParent())
                            .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                            .extraData("id", comment.getParent()));
            comment1.setReplyCount(comment1.getReplyCount() - 1);
            commentRepo.save(comment1);
        } else {
            List<Comment> comments = commentRepo.findByParent(comment.getId());
            if (comments != null) commentRepo.deleteAll(comments);
        }
        commentRepo.delete(comment);
    }
}
