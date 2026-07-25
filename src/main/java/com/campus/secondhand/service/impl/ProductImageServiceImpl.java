package com.campus.secondhand.service.impl;

import com.campus.secondhand.service.ProductImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    @Override
    public String upload(MultipartFile file) {
        try{
            String filename = UUID.randomUUID()+"_"+ file.getOriginalFilename();

            File dir = new File("upload/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file.transferTo(new File(dir,filename));
            return "/upload/"+filename;
        } catch (Exception e) {
            throw new RuntimeException("图片上传失败");
        }

    }









}
