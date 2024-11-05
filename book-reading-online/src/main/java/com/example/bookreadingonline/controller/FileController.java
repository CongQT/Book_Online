package com.example.bookreadingonline.controller;

import com.example.bookreadingonline.payload.request.LoginRequest;
import com.example.bookreadingonline.payload.response.FileResponse;
import com.example.bookreadingonline.payload.response.LoginResponse;
import com.example.bookreadingonline.payload.response.base.BaseResponse;
import com.example.bookreadingonline.service.FileService;
import com.example.bookreadingonline.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public BaseResponse<FileResponse> uploadFile(@RequestParam MultipartFile file)
            throws IOException {
        return BaseResponse.of(fileService.uploadFile(file));
    }
}
