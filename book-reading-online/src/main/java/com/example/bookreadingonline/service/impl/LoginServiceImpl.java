package com.example.bookreadingonline.service.impl;

import com.example.bookreadingonline.config.properties.JwtProperties;
import com.example.bookreadingonline.entity.User;
import com.example.bookreadingonline.payload.request.LoginRequest;
import com.example.bookreadingonline.payload.response.LoginResponse;
import com.example.bookreadingonline.security.core.userdetails.impl.UserDetailsImpl;
import com.example.bookreadingonline.service.LoginService;
import com.example.bookreadingonline.util.JwtUtils;
import com.example.bookreadingonline.util.MyServletUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Log4j2
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final JwtProperties jwtProperties;

    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername().toLowerCase(),
                        request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        return login(user);
    }

    @Override
    public LoginResponse login(User user) {
        LocalDateTime now = MyServletUtils.getRequestDateTime();

        LocalDateTime expiryTime = now.plus(jwtProperties.getExpiryDuration());
        String accessToken = JwtUtils.generateJwtToken(
                user.getUsername(),
                now.plus(jwtProperties.getExpiryDuration()),
                jwtProperties.getSecretKey()
        );

        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .accessToken(accessToken)
                .expiryTime(expiryTime)
                .build();
    }
}
