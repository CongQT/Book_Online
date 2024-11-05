package com.example.bookreadingonline.service;

public interface FileStoreService {

    String saveFile(String fileKey, String contentType, byte[] content);

    String getFileUrl(String fileKey);
}
