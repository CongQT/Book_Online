package com.example.bookreadingonline.service.impl;

import com.example.bookreadingonline.constant.ErrorCode;
import com.example.bookreadingonline.entity.*;
import com.example.bookreadingonline.exception.BadRequestException;
import com.example.bookreadingonline.exception.NotFoundException;
import com.example.bookreadingonline.payload.filter.BookFilter;
import com.example.bookreadingonline.payload.filter.BaseEntityFilter;
import com.example.bookreadingonline.payload.filter.CategoryBookFilter;
import com.example.bookreadingonline.payload.filter.CommentFilter;
import com.example.bookreadingonline.payload.request.BookRequest;
import com.example.bookreadingonline.payload.request.CategoryBookRequest;
import com.example.bookreadingonline.payload.response.*;
import com.example.bookreadingonline.payload.response.base.PageResponse;
import com.example.bookreadingonline.repository.*;
import com.example.bookreadingonline.security.core.userdetails.impl.UserDetailsImpl;
import com.example.bookreadingonline.service.BookService;
import com.example.bookreadingonline.service.FileService;
import com.example.bookreadingonline.util.MyStringUtils;
import com.example.bookreadingonline.util.SecurityUtils;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepo;
    private final CategoryBookRepository categoryBookRepo;
    private final AuthorRepository authorRepo;
    private final CategoryRepository categoryRepo;
    private final HistoryRepository historyRepo;
    private final ChapterRepository chapterRepo;
    private final FeedbackRepository feedbackRepo;

    private final FileService fileService;

    private final ModelMapper modelMapper;

    @Override
    public JpaRepository<Book, Integer> getRepository() {
        return bookRepo;
    }

    @Override
    public BookResponse createBook(BookRequest request) {
        List<CategoryBook> categoryBooks = new ArrayList<>();
        Book book = Book.builder()
                .title(request.getTitle())
                .thumbnail(request.getThumbnail())
                .summary(request.getSummary())
                .avgRating(0.0)
                .feedbackCount(0)
                .view(0)
                .status("Chưa hoàn thành")
                .authorId(request.getAuthorId())
                .banner(request.getBanner())
                .build();
        bookRepo.save(book);
        request.getCategoryBooks().forEach(categoryBookRequest -> {
            CategoryBook categoryBook = CategoryBook.builder()
                    .categoryId(categoryBookRequest.getCategoryId())
                    .bookId(book.getId())
                    .build();
            categoryBooks.add(categoryBook);
        });
        categoryBookRepo.saveAll(categoryBooks);
        book.setCategoryBooks(categoryBooks);
        BookResponse bookResponse = modelMapper.map(book, BookResponse.class);
        bookResponse.setThumbnailUrl(fileService.getFileUrl(book.getThumbnail()));
        if (book.getBanner() != null) bookResponse.setBannerUrl(fileService.getFileUrl(book.getBanner()));
        if (book.getAuthorId() != null) {
            Author author = authorRepo.findById(book.getAuthorId())
                    .orElseThrow(() -> new NotFoundException("Not found Author with id: " + book.getAuthorId())
                            .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                            .extraData("id", book.getAuthorId()));
            bookResponse.setAuthor(modelMapper.map(author, AuthorResponse.class));
        }
        if (book.getCategoryBooks() != null) {
            bookResponse.getCategoryBooks().forEach(categoryBookResponse -> {
                Category category = categoryRepo.findById(categoryBookResponse.getCategoryId())
                        .orElseThrow(() -> new NotFoundException("Not found Book with id: " + categoryBookResponse.getCategoryId())
                                .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                                .extraData("id", categoryBookResponse.getCategoryId()));
                categoryBookResponse.setCategoryName(category.getName());

            });
        }
        return bookResponse;
    }

    @Override
    @Transactional
    public BookResponse updateBook(BookRequest request) {
        Book book = bookRepo.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Not found Book with id: " + request.getId())
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", request.getId()));
        modelMapper.map(request, book);
        bookRepo.save(book);
        List<CategoryBook> categoryBooks = new ArrayList<>();
        if (request.getCategoryBooks() != null) {
            if (book.getCategoryBooks() != null) {
                book.getCategoryBooks().forEach(categoryBook -> categoryBookRepo.delete(categoryBook));
            }
            request.getCategoryBooks().forEach(categoryBookRequest -> {
                CategoryBook categoryBook = CategoryBook.builder()
                        .categoryId(categoryBookRequest.getCategoryId())
                        .bookId(book.getId())
                        .build();
                categoryBooks.add(categoryBook);
            });
            categoryBookRepo.saveAll(categoryBooks);
            book.setCategoryBooks(categoryBooks);
        }
        BookResponse bookResponse = modelMapper.map(book, BookResponse.class);
        bookResponse.setThumbnailUrl(fileService.getFileUrl(book.getThumbnail()));
        if (book.getBanner() != null) bookResponse.setBannerUrl(fileService.getFileUrl(book.getBanner()));
        if (book.getAuthorId() != null) {
            Author author = authorRepo.findById(book.getAuthorId())
                    .orElseThrow(() -> new NotFoundException("Not found Author with id: " + book.getAuthorId())
                            .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                            .extraData("id", book.getAuthorId()));
            bookResponse.setAuthor(modelMapper.map(author, AuthorResponse.class));
        }
        if (book.getCategoryBooks() != null) {
            bookResponse.getCategoryBooks().forEach(categoryBookResponse -> {
                Category category = categoryRepo.findById(categoryBookResponse.getCategoryId())
                        .orElseThrow(() -> new NotFoundException("Not found Book with id: " + categoryBookResponse.getCategoryId())
                                .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                                .extraData("id", categoryBookResponse.getCategoryId()));
                categoryBookResponse.setCategoryName(category.getName());

            });
        }
        return bookResponse;
    }

    @Override
    @Transactional
    public BookResponse getBook(Integer id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found Book with id: " + id)
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", id));
        BookResponse bookResponse = modelMapper.map(book, BookResponse.class);
        bookResponse.setThumbnailUrl(fileService.getFileUrl(book.getThumbnail()));
        if (book.getBanner() != null) bookResponse.setBannerUrl(fileService.getFileUrl(book.getBanner()));
        if (book.getAuthorId() != null) {
            Author author = authorRepo.findById(book.getAuthorId())
                    .orElseThrow(() -> new NotFoundException("Not found Author with id: " + book.getAuthorId())
                            .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                            .extraData("id", book.getAuthorId()));
            bookResponse.setAuthor(modelMapper.map(author, AuthorResponse.class));
        }
        if (book.getCategoryBooks() != null) {
            bookResponse.getCategoryBooks().forEach(categoryBookResponse -> {
                Category category = categoryRepo.findById(categoryBookResponse.getCategoryId())
                        .orElseThrow(() -> new NotFoundException("Not found Book with id: " + categoryBookResponse.getCategoryId())
                                .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                                .extraData("id", categoryBookResponse.getCategoryId()));
                categoryBookResponse.setCategoryName(category.getName());

            });
        }
        return bookResponse;
    }

    @Override
    public void deleteBook(Integer id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found Book with id: " + id)
                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                        .extraData("id", id));
        bookRepo.delete(book);
        List<Chapter> chapters = chapterRepo.findByBookId(id);
        if (chapters != null) chapterRepo.deleteAll(chapters);
        List<Feedback> feedbacks = feedbackRepo.findByBookId(id);
        if (feedbacks != null) feedbackRepo.deleteAll(feedbacks);

    }

    @Override
    @Transactional
    public PageResponse<BookResponse> listSearch(String title, String status, Integer authorId, Integer categoryId, Pageable pageable) {
        BaseEntityFilter<BookFilter> filter = BaseEntityFilter.of(Lists.newArrayList(),
                pageable);
        if (StringUtils.isNotBlank(title) || status != null || authorId != null || categoryId != null) {
            BookFilter bookFilter = new BookFilter();
            if (StringUtils.isNotBlank(title)) {
                bookFilter.setTitleLk(MyStringUtils.buildSqlLikePattern(title));
            }
            if (status != null) {
                bookFilter.setStatusLk(status);
            }
            if (authorId != null) {
                bookFilter.setAuthorId(authorId);
            }
            if (categoryId != null) {
                List<CategoryBook> categoryBooks = categoryBookRepo.findByCategoryId(categoryId);
                if (categoryBooks != null && !categoryBooks.isEmpty()) {
                    categoryBooks.forEach(categoryBook -> {
                        bookFilter.setCategoryBooks(CategoryBookFilter.builder()
                                .categoryId(categoryBook.getCategoryId()).build());
                    });
                }
                else bookFilter.setCategoryBooks(CategoryBookFilter.builder()
                        .categoryId(0).build());
            }
            filter.getFilters().add(bookFilter);
        }

        return PageResponse
                .toResponse(filter(filter),
                        book -> {
                            BookResponse bookResponse = modelMapper.map(book, BookResponse.class);
                            bookResponse.setThumbnailUrl(fileService.getFileUrl(book.getThumbnail()));
                            if (book.getBanner() != null) bookResponse.setBannerUrl(fileService.getFileUrl(book.getBanner()));
                            if (book.getAuthorId() != null) {
                                Author author = authorRepo.findById(book.getAuthorId())
                                        .orElseThrow(() -> new NotFoundException("Not found Author with id: " + book.getAuthorId())
                                                .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                                                .extraData("id", book.getAuthorId()));
                                bookResponse.setAuthor(modelMapper.map(author, AuthorResponse.class));
                            }
                            if (book.getCategoryBooks() != null) {
                                bookResponse.getCategoryBooks().forEach(categoryBookResponse -> {
                                    Category category = categoryRepo.findById(categoryBookResponse.getCategoryId())
                                            .orElseThrow(() -> new NotFoundException("Not found Book with id: " + categoryBookResponse.getCategoryId())
                                                    .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                                                    .extraData("id", categoryBookResponse.getCategoryId()));
                                    categoryBookResponse.setCategoryName(category.getName());

                                });
                            }
                            Chapter chapter = chapterRepo.findChapterWithHighestOrder(book.getId());
                            if (chapter != null){
                                bookResponse.setNewChapter(modelMapper.map(chapter, NewChapterResponse.class));

                            }
                            return bookResponse;
                        });
    }

    @Override
    @Transactional
    public PageResponse<BookHistoryResponse> list(Pageable pageable) {
        User user = SecurityUtils.getUserDetails(UserDetailsImpl.class).getUser();
        List<History> distinctHistories = historyRepo.findByUserId(user.getId())
                .stream()
                .collect(Collectors.groupingBy(History::getBookId,
                        Collectors.maxBy(Comparator.comparingInt(History::getId))))
                .values()
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparingInt(History::getId).reversed())
                .toList();
        if (distinctHistories.isEmpty()) {
            throw new BadRequestException(
                    "history is null").errorCode(ErrorCode.ENTITY_NOT_FOUND);
        }

        BaseEntityFilter<BookFilter> filter = BaseEntityFilter.of(Lists.newArrayList(), pageable);
        distinctHistories.forEach(distinctHistory -> {
            filter.getFilters().add(BookFilter.builder()
                    .id(distinctHistory.getBookId())
                    .build());
        });
        PageResponse<BookHistoryResponse> pageResponse = PageResponse.toResponse(filter(filter), book -> {
                    BookHistoryResponse bookResponse = modelMapper.map(book, BookHistoryResponse.class);
                    bookResponse.setThumbnailUrl(fileService.getFileUrl(book.getThumbnail()));

                    distinctHistories.stream()
                            .filter(distinctHistory -> distinctHistory.getBookId().equals(book.getId()))
                            .findFirst()
                            .ifPresent(distinctHistory -> bookResponse.setChapterId(distinctHistory.getChapterId()));
                    Chapter chapter = chapterRepo.findById(bookResponse.getChapterId()).orElseThrow(() -> new NotFoundException("Not found Author with id: " + bookResponse.getChapterId())
                            .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                            .extraData("id", bookResponse.getChapterId()));
                    bookResponse.setChapter(modelMapper.map(chapter, ChapterHistoryResponse.class));
                    if (book.getAuthorId() != null) {
                        Author author = authorRepo.findById(book.getAuthorId())
                                .orElseThrow(() -> new NotFoundException("Not found Author with id: " + book.getAuthorId())
                                        .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                                        .extraData("id", book.getAuthorId()));
                        bookResponse.setAuthor(modelMapper.map(author, AuthorResponse.class));
                    }

                    if (book.getCategoryBooks() != null) {
                        bookResponse.getCategoryBooks().forEach(categoryBookResponse -> {
                            Category category = categoryRepo.findById(categoryBookResponse.getCategoryId())
                                    .orElseThrow(() -> new NotFoundException("Not found Book with id: " + categoryBookResponse.getCategoryId())
                                            .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                                            .extraData("id", categoryBookResponse.getCategoryId()));
                            categoryBookResponse.setCategoryName(category.getName());
                        });
                    }

                    return bookResponse;
                });
        List<BookHistoryResponse> sortedResponses = distinctHistories.stream()
                .flatMap(distinctHistory -> pageResponse.getContent().stream()
                        .filter(response -> response.getId().equals(distinctHistory.getBookId())))
                .collect(Collectors.toList());

        return PageResponse.<BookHistoryResponse>builder()
                .content(sortedResponses)
                .number(pageResponse.getNumber())
                .size(pageResponse.getSize())
                .numberOfElements(pageResponse.getNumberOfElements())
                .first(pageResponse.isFirst())
                .last(pageResponse.isLast())
                .totalPages(pageResponse.getTotalPages())
                .totalElements(pageResponse.getTotalElements())
                .build();
    }

    @Override
    @Transactional
    public PageResponse<BookResponse> listBookBanner(Pageable pageable) {
        Page<Book> booksWithBanner = bookRepo.findByBannerIsNotNull(pageable);

        return PageResponse
                .toResponse(booksWithBanner,
                        book -> {
                            BookResponse bookResponse = modelMapper.map(book, BookResponse.class);
                            bookResponse.setThumbnailUrl(fileService.getFileUrl(book.getThumbnail()));
                            bookResponse.setBannerUrl(fileService.getFileUrl(book.getBanner()));
                            if (book.getAuthorId() != null) {
                                Author author = authorRepo.findById(book.getAuthorId())
                                        .orElseThrow(() -> new NotFoundException("Not found Author with id: " + book.getAuthorId())
                                                .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                                                .extraData("id", book.getAuthorId()));
                                bookResponse.setAuthor(modelMapper.map(author, AuthorResponse.class));
                            }
                            if (book.getCategoryBooks() != null) {
                                bookResponse.getCategoryBooks().forEach(categoryBookResponse -> {
                                    Category category = categoryRepo.findById(categoryBookResponse.getCategoryId())
                                            .orElseThrow(() -> new NotFoundException("Not found Book with id: " + categoryBookResponse.getCategoryId())
                                                    .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                                                    .extraData("id", categoryBookResponse.getCategoryId()));
                                    categoryBookResponse.setCategoryName(category.getName());

                                });
                            }
                            return bookResponse;
                        });
    }

    @Override
    @Transactional
    public PageResponse<BookResponse> listNew(Pageable pageable) {
        Page<Book> booksPage = bookRepo.findAllBooksSortedByChapterOrder(pageable);

        return PageResponse
                .toResponse(booksPage,
                        book -> {
                            BookResponse bookResponse = modelMapper.map(book, BookResponse.class);
                            bookResponse.setThumbnailUrl(fileService.getFileUrl(book.getThumbnail()));
                            if (book.getAuthorId() != null) {
                                Author author = authorRepo.findById(book.getAuthorId())
                                        .orElseThrow(() -> new NotFoundException("Not found Author with id: " + book.getAuthorId())
                                                .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                                                .extraData("id", book.getAuthorId()));
                                bookResponse.setAuthor(modelMapper.map(author, AuthorResponse.class));
                            }
                            if (book.getCategoryBooks() != null) {
                                bookResponse.getCategoryBooks().forEach(categoryBookResponse -> {
                                    Category category = categoryRepo.findById(categoryBookResponse.getCategoryId())
                                            .orElseThrow(() -> new NotFoundException("Not found Book with id: " + categoryBookResponse.getCategoryId())
                                                    .errorCode(ErrorCode.ENTITY_NOT_FOUND)
                                                    .extraData("id", categoryBookResponse.getCategoryId()));
                                    categoryBookResponse.setCategoryName(category.getName());

                                });
                            }
                            Chapter chapter = chapterRepo.findChapterWithHighestOrder(book.getId());
                            if (chapter != null){
                                bookResponse.setNewChapter(modelMapper.map(chapter, NewChapterResponse.class));

                            }
                            return bookResponse;
                        });
    }


}
