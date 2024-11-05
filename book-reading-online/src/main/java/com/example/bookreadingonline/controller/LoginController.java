package com.example.bookreadingonline.controller;

import com.example.bookreadingonline.payload.request.LoginRequest;
import com.example.bookreadingonline.payload.response.LoginResponse;
import com.example.bookreadingonline.payload.response.base.BaseResponse;
import com.example.bookreadingonline.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping
    public BaseResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return BaseResponse.of(loginService.login(request));
    }

}
