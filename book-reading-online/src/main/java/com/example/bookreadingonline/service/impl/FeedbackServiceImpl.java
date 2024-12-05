package com.example.bookreadingonline.service.impl;

import com.example.bookreadingonline.constant.ErrorCode;
import com.example.bookreadingonline.entity.Book;
import com.example.bookreadingonline.entity.Feedback;
import com.example.bookreadingonline.entity.User;
import com.example.bookreadingonline.exception.BadRequestException;
import com.example.bookreadingonline.exception.NotFoundException;
import com.example.bookreadingonline.payload.filter.BaseEntityFilter;
import com.example.bookreadingonline.payload.filter.ChapterFilter;
import com.example.bookreadingonline.payload.filter.FeedbackFilter;
import com.example.bookreadingonline.payload.request.FeedbackRequest;
import com.example.bookreadingonline.payload.response.ChapterResponse;
import com.example.bookreadingonline.payload.response.FeedbackResponse;
import com.example.bookreadingonline.payload.response.UserInfoResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import com.example.bookreadingonline.repository.BookRepository;
import com.example.bookreadingonline.repository.FeedbackRepository;
import com.example.bookreadingonline.repository.UserRepository;
import com.example.bookreadingonline.security.core.userdetails.impl.UserDetailsImpl;
import com.example.bookreadingonline.service.FeedbackService;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepo;
    private final UserRepository userRepo;
    private final BookRepository bookRepo;

    private final FileService fileService;

    private final ModelMapper modelMapper;

    @Override
    public JpaRepository<Feedback, Integer> getRepository() {
        return feedbackRepo;
    }

    @Override
    @Transactional
    public FeedbackResponse createFeedback(Integer bookId, FeedbackRequest request) {
        User user = SecurityUtils.getUserDetails(UserDetailsImpl.class).getUser();
        Feedback feedbackCheck = feedbackRepo.findByUserIdAndBookId(user.getId(), bookId);
        if (feedbackCheck != null) {
            throw new BadRequestException(
                    "Feedback existed with id: " + feedbackCheck.getId()).errorCode(ErrorCode.ENTITY_EXISTED);
        }
        Feedback feedback = Feedback.builder()
                .content(request.getContent())
                .rating(request.getRating())
                .bookId(bookId)
                .userId(user.getId())
                .build();
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Not found Book with id: " + bookId)
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", bookId));
        book.setAvgRating(
                (book.getAvgRating() * book.getFeedbackCount() + feedback.getRating())
                        / (book.getFeedbackCount() + 1));
        book.setFeedbackCount(book.getFeedbackCount() + 1);
        bookRepo.save(book);
        feedbackRepo.save(feedback);
        return modelMapper.map(feedback, FeedbackResponse.class);
    }

    @Override
    @Transactional
    public FeedbackResponse updateFeedback(FeedbackRequest request) {
        Feedback feedback = feedbackRepo.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Not found Feeback with id: " + request.getId())
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", request.getId()));
        Book book = bookRepo.findById(feedback.getBookId())
                .orElseThrow(() -> new NotFoundException("Not found Book with id: " + feedback.getBookId())
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", feedback.getBookId()));
        book.setAvgRating((book.getAvgRating() * book.getFeedbackCount() - feedback.getRating() + request.getRating())
                / book.getFeedbackCount());
        modelMapper.map(request, feedback);
        feedbackRepo.save(feedback);
        bookRepo.save(book);
        return modelMapper.map(feedback, FeedbackResponse.class);
    }

    @Override
    @Transactional
    public FeedbackResponse getUserFeedback(Integer bookId) {
        User user = SecurityUtils.getUserDetails(UserDetailsImpl.class).getUser();
        Feedback feedback = feedbackRepo.findByUserIdAndBookId(user.getId(), bookId);
        FeedbackResponse feedbackResponse = modelMapper.map(feedback, FeedbackResponse.class);
        UserInfoResponse userInfoResponse = modelMapper.map(user, UserInfoResponse.class);
        userInfoResponse.setAvatar_url(fileService.getFileUrl(user.getAvatar()));
        feedbackResponse.setUser(userInfoResponse);
        String updateTimeString = feedback.getCreatedAt().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime updateTime = LocalDateTime.parse(updateTimeString, formatter);
        LocalDateTime now = LocalDateTime.now();
        feedbackResponse.setLastUpdated(roundTime(updateTime, now));
        return feedbackResponse;
    }

    @Override
    @Transactional
    public PageResponse<FeedbackResponse> list(Integer bookId, Pageable pageable) {
        BaseEntityFilter<FeedbackFilter> filter = BaseEntityFilter.of(Lists.newArrayList(), pageable);
        filter.getFilters().add(FeedbackFilter.builder()
                .bookId(bookId)
                .build());
        return PageResponse
                .toResponse(filter(filter), feedback -> {
                    User user = userRepo.findById(feedback.getUserId())
                            .orElseThrow(() -> new NotFoundException("Not found User with id: " + feedback.getUserId())
                                    .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                                    .extraData("id", feedback.getUserId()));
                    FeedbackResponse feedbackResponse = modelMapper.map(feedback, FeedbackResponse.class);
                    UserInfoResponse userInfoResponse = modelMapper.map(user, UserInfoResponse.class);
                    userInfoResponse.setAvatar_url(fileService.getFileUrl(user.getAvatar()));
                    feedbackResponse.setUser(userInfoResponse);
                    String updateTimeString = feedback.getCreatedAt().toString();
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                    LocalDateTime updateTime = LocalDateTime.parse(updateTimeString, formatter);
                    LocalDateTime now = LocalDateTime.now();
                    feedbackResponse.setLastUpdated(roundTime(updateTime, now));
                    return feedbackResponse;
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
}
