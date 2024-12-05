package com.example.bookreadingonline.service.impl;

import com.example.bookreadingonline.constant.ErrorCode;
import com.example.bookreadingonline.entity.Role;
import com.example.bookreadingonline.entity.User;
import com.example.bookreadingonline.exception.BadRequestException;
import com.example.bookreadingonline.payload.request.RegistrationRequest;
import com.example.bookreadingonline.repository.UserRepository;
import com.example.bookreadingonline.service.RoleService;
import com.example.bookreadingonline.service.SignupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignupService {

    private final UserRepository userRepo;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void signup(RegistrationRequest request) {
        userRepo.findFirstByUsername(request.getUsername())
                .ifPresent(user -> {
                    throw new BadRequestException("Existed user with username: " + request.getUsername())
                            .errorCode(ErrorCode.USERNAME_EXISTED)
                            .extraData("username", request.getUsername());
                });

        Role role = roleService.findById(2);
        if (!role.isAllowedSignUp()) {
            throw new BadRequestException("Can not signup with role: " + role.getName())
                    .errorCode(ErrorCode.INVALID_INPUT_DATA)
                    .extraData("role", role.getName());
        }

        User user = User.builder()
                .username(request.getUsername())
                .role(role)
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .name(request.getUsername())
                .build();
        userRepo.save(user);
    }
}
