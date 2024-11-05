package com.example.bookreadingonline.service;

import com.example.bookreadingonline.payload.response.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    FileResponse uploadFile(MultipartFile file) throws IOException;

    String getFileUrl(String fileKey);
}
