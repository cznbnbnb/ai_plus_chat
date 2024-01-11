package com.gdut.ai.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    public String storeFile(MultipartFile file) throws IOException;
}
