package com.gdut.ai.controller;

import com.gdut.ai.common.R;
import com.gdut.ai.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public R<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            // 处理文件上传，保存文件，返回URL
            String url = fileStorageService.storeFile(file);
            return R.success(url);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return R.error("文件上传失败");
        }
    }
}
