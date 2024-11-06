package com.example.bookreadingonline.service;

import com.example.bookreadingonline.entity.User;
import com.example.bookreadingonline.payload.request.LoginRequest;
import com.example.bookreadingonline.payload.request.RegistrationRequest;
import com.example.bookreadingonline.payload.request.UserInfoRequest;
import com.example.bookreadingonline.payload.response.LoginResponse;
import com.example.bookreadingonline.payload.response.UserInfoResponse;

public interface UserService extends BaseEntityService<User, Integer>{
    User findByUsername(String username);

    UserInfoResponse getUserInfo();

    UserInfoResponse updateUserInfo(UserInfoRequest request);
}
