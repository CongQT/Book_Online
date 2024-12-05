package com.example.bookreadingonline.controller;

import com.example.bookreadingonline.payload.request.FeedbackRequest;
import com.example.bookreadingonline.payload.request.ReactRequest;
import com.example.bookreadingonline.payload.request.RegistrationRequest;
import com.example.bookreadingonline.payload.response.base.BaseResponse;
import com.example.bookreadingonline.service.LikeService;
import com.example.bookreadingonline.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public BaseResponse<Object> like(@RequestBody @Valid ReactRequest request,
                                     @RequestParam(name = "commentId") Integer commentId) {
        likeService.createReact(request, commentId);
        return BaseResponse.of(Collections.emptyMap());
    }

}
