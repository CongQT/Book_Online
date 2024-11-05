package com.example.bookreadingonline.service.impl;

import com.example.bookreadingonline.config.properties.JwtProperties;
import com.example.bookreadingonline.constant.ErrorCode;
import com.example.bookreadingonline.entity.Role;
import com.example.bookreadingonline.entity.User;
import com.example.bookreadingonline.exception.BadRequestException;
import com.example.bookreadingonline.exception.NotFoundException;
import com.example.bookreadingonline.payload.request.LoginRequest;
import com.example.bookreadingonline.payload.request.RegistrationRequest;
import com.example.bookreadingonline.payload.response.LoginResponse;
import com.example.bookreadingonline.repository.UserRepository;
import com.example.bookreadingonline.security.core.userdetails.impl.UserDetailsImpl;
import com.example.bookreadingonline.service.RoleService;
import com.example.bookreadingonline.service.UserService;
import com.example.bookreadingonline.util.JwtUtils;
import com.example.bookreadingonline.util.MyServletUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepo;


  @Override
  public JpaRepository<User, Integer> getRepository() {
    return userRepo;
  }

  @Override
  public User findByUsername(String username) {
    return userRepo.findFirstByUsername(username)
        .orElseThrow(() -> new NotFoundException("Not found user with username: " + username)
            .errorCode(ErrorCode.USER_NOT_FOUND)
            .extraData("username", username));
  }



}