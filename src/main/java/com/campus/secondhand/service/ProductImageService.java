package com.campus.secondhand.service;

import org.springframework.web.multipart.MultipartFile;

public interface ProductImageService {

    String upload(MultipartFile file);
}
