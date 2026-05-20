package com.learning.productservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    public String uploadFile(MultipartFile image);
    public void deleteFile(String fileUrl) ;
}
