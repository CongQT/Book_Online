package com.example.bookreadingonline.service.impl;

import com.example.bookreadingonline.constant.ErrorCode;
import com.example.bookreadingonline.entity.*;
import com.example.bookreadingonline.exception.BadRequestException;
import com.example.bookreadingonline.exception.NotFoundException;
import com.example.bookreadingonline.payload.request.RegistrationRequest;
import com.example.bookreadingonline.repository.BookRepository;
import com.example.bookreadingonline.repository.ChapterRepository;
import com.example.bookreadingonline.repository.HistoryRepository;
import com.example.bookreadingonline.repository.UserRepository;
import com.example.bookreadingonline.security.core.userdetails.impl.UserDetailsImpl;
import com.example.bookreadingonline.service.HistoryService;
import com.example.bookreadingonline.service.RoleService;
import com.example.bookreadingonline.service.SignupService;
import com.example.bookreadingonline.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepo;
    private final BookRepository bookRepo;
    private final ChapterRepository chapterRepo;

    @Override
    public JpaRepository<History, Integer> getRepository() {
        return historyRepo;
    }

    @Override
    @Transactional
    public void saveHistory(Integer chapterId) {
        User user = SecurityUtils.getUserDetails(UserDetailsImpl.class).getUser();
        Chapter chapter = chapterRepo.findById(chapterId).orElseThrow(() -> new NotFoundException("Not found chap with id: " + chapterId)
                .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                .extraData("id", chapterId));
        History history = History.builder()
                .userId(user.getId())
                .bookId(chapter.getBookId())
                .chapterId(chapterId)
                .build();
        historyRepo.save(history);
    }

    @Override
    @Transactional
    public void updateView(Integer chapterId) {
        Chapter chapter = chapterRepo.findById(chapterId).orElseThrow(() -> new NotFoundException("Not found chap with id: " + chapterId)
                .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                .extraData("id", chapterId));
        Book book = bookRepo.findById(chapter.getBookId())
                .orElseThrow(() -> new NotFoundException("Not found Book with id: " + chapter.getBookId())
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", chapter.getBookId()));
        book.setView(book.getView() + 1);
        bookRepo.save(book);
    }
}
