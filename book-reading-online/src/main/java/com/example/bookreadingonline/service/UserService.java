package com.example.bookreadingonline.service;

import com.example.bookreadingonline.entity.User;
import com.example.bookreadingonline.payload.request.LoginRequest;
import com.example.bookreadingonline.payload.request.RegistrationRequest;
import com.example.bookreadingonline.payload.response.LoginResponse;

public interface UserService extends BaseEntityService<User, Integer>{
    User findByUsername(String username);

}
