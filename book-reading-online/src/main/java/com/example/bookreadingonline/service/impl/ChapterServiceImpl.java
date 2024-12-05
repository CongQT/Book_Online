package com.example.bookreadingonline.service.impl;

import com.example.bookreadingonline.constant.ErrorCode;
import com.example.bookreadingonline.entity.Book;
import com.example.bookreadingonline.entity.Chapter;
import com.example.bookreadingonline.exception.NotFoundException;
import com.example.bookreadingonline.payload.filter.AuthorFilter;
import com.example.bookreadingonline.payload.filter.BaseEntityFilter;
import com.example.bookreadingonline.payload.filter.ChapterFilter;
import com.example.bookreadingonline.payload.request.ChapterRequest;
import com.example.bookreadingonline.payload.response.AuthorResponse;
import com.example.bookreadingonline.payload.response.BookResponse;
import com.example.bookreadingonline.payload.response.ChapterResponse;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import com.example.bookreadingonline.repository.BookRepository;
import com.example.bookreadingonline.repository.ChapterRepository;
import com.example.bookreadingonline.service.ChapterService;
import com.example.bookreadingonline.service.FileService;
import com.example.bookreadingonline.util.MyStringUtils;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepo;
    private final BookRepository bookRepo;

    private final FileService fileService;

    private final ModelMapper modelMapper;

    @Override
    public JpaRepository<Chapter, Integer> getRepository() {
        return chapterRepo;
    }

    @Override
    public ChapterResponse createChapter(Integer bookId, ChapterRequest request) {
        Chapter chapter = Chapter.builder()
                .title(request.getTitle())
                .fileKey(request.getFileKey())
                .bookId(bookId)
                .orderChap(request.getOrderChap())
                .build();
        chapterRepo.save(chapter);
        return modelMapper.map(chapter, ChapterResponse.class);
    }

    @Override
    public ChapterResponse updateChapter(Integer bookId, ChapterRequest request) {
        Chapter chapter = chapterRepo.findByIdAndBookId(request.getId(), bookId);
        modelMapper.map(request, chapter);
        chapterRepo.save(chapter);
        return modelMapper.map(chapter, ChapterResponse.class);
    }

    @Override
    @Transactional
    public ChapterResponse getChapter(Integer bookId, Integer order) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Not found book with id: " + bookId)
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", bookId));
        Chapter chapter = chapterRepo.findByOrderChapAndBookId(order, bookId);
        ChapterResponse chapterResponse = modelMapper.map(chapter, ChapterResponse.class);
        String fileUrl = fileService.getFileUrl(chapter.getFileKey());
        chapterResponse.setFileUrl(fileUrl);
        chapterResponse.setBook(modelMapper.map(book, BookResponse.class));
        List<Integer> chapterOrder = chapterRepo.findByBookId(bookId).stream() 
                .map(Chapter::getOrderChap) 
                .sorted(Comparator.reverseOrder()) 
                .toList();
        log.info("Chapter"+ chapterOrder);
        if (chapterOrder.get(0).equals(order) && !chapterOrder.get(chapterOrder.size() - 1).equals(order)) {
            chapterResponse.setOrderNext(null);
            chapterResponse.setOrderPrevious(order - 1);
        } else if (!chapterOrder.get(0).equals(order) && chapterOrder.get(chapterOrder.size() - 1).equals(order)) {
            chapterResponse.setOrderPrevious(null);
            chapterResponse.setOrderNext(order + 1);
        } else if (chapterOrder.get(0).equals(order) && chapterOrder.get(chapterOrder.size() - 1).equals(order)) {
            chapterResponse.setOrderPrevious(null);
            chapterResponse.setOrderNext(null);
        }
        else {
            chapterResponse.setOrderPrevious(order - 1);
            chapterResponse.setOrderNext(order + 1);
        }
        return chapterResponse;
    }

    @Override
    public void deleteChapter(Integer id) {
        Chapter chapter = chapterRepo.findById(id).orElseThrow(() -> new NotFoundException("Not found chap with id: " + id)
                .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                .extraData("id", id));
        chapterRepo.delete(chapter);
    }

    @Override
    public PageResponse<ChapterResponse> listSearch(Integer bookId, Pageable pageable) {
        BaseEntityFilter<ChapterFilter> filter = BaseEntityFilter.of(Lists.newArrayList(), pageable);
        filter.getFilters().add(ChapterFilter.builder()
                .bookId(bookId)
                .build());
        return PageResponse
                .toResponse(filter(filter), chapter -> modelMapper.map(chapter, ChapterResponse.class));
    }


}
