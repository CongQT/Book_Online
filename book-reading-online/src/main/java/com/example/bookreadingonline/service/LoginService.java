package com.example.bookreadingonline.service;

import com.example.bookreadingonline.entity.User;
import com.example.bookreadingonline.payload.request.LoginRequest;
import com.example.bookreadingonline.payload.response.LoginResponse;

public interface LoginService {

    LoginResponse login(LoginRequest request);

    LoginResponse login(User user);
}
