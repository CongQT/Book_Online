package com.example.bookreadingonline.controller;

import com.example.bookreadingonline.payload.request.BookRequest;
import com.example.bookreadingonline.payload.request.LoginRequest;
import com.example.bookreadingonline.payload.request.UserInfoRequest;
import com.example.bookreadingonline.payload.response.BookResponse;
import com.example.bookreadingonline.payload.response.LoginResponse;
import com.example.bookreadingonline.payload.response.UserInfoResponse;
import com.example.bookreadingonline.payload.response.base.BaseResponse;
import com.example.bookreadingonline.service.LoginService;
import com.example.bookreadingonline.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/info")
    public BaseResponse<UserInfoResponse> info() {
        return BaseResponse.of(userService.getUserInfo());
    }

    @PutMapping("/update")
    public BaseResponse<UserInfoResponse> updateUser(
            @RequestBody @Valid UserInfoRequest request
    ) {
        return BaseResponse.of(userService.updateUserInfo(request));
    }
}
