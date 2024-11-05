package com.example.bookreadingonline.controller;

import com.example.bookreadingonline.payload.request.LoginRequest;
import com.example.bookreadingonline.payload.request.RegistrationRequest;
import com.example.bookreadingonline.payload.response.LoginResponse;
import com.example.bookreadingonline.payload.response.base.BaseResponse;
import com.example.bookreadingonline.service.LoginService;
import com.example.bookreadingonline.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/public/signup")
@RequiredArgsConstructor
public class SignupController {
    private final SignupService signupService;

    @PostMapping
    public BaseResponse<Object> signup(@RequestBody @Valid RegistrationRequest request) {
        signupService.signup(request);
        return BaseResponse.of(Collections.emptyMap());
    }

}
