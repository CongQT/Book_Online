package com.example.bookreadingonline.service.impl;

import com.example.bookreadingonline.payload.response.FileResponse;
import com.example.bookreadingonline.service.FileService;
import com.example.bookreadingonline.service.FileStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Log4j2
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileStoreService fileStoreService;

    @Override
    public FileResponse uploadFile(MultipartFile file) throws IOException {
        String fileKey = fileStoreService.saveFile(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes()
        );
        return FileResponse.builder()
                .key(fileKey)
                .build();
    }

    @Override
    public String getFileUrl(String fileKey) {
        return StringUtils.isNotBlank(fileKey) ? fileStoreService.getFileUrl(fileKey) : null;
    }
}
