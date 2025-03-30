package com.example.studentservice.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();

            Map<String, Object> params = ObjectUtils.asMap(
                    "resource_type", "auto",  // Use "auto" to let Cloudinary detect type// Force format to PDF
                    "type", "upload"         // Ensure upload type
            );

            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    params
            );

            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("File upload failed!", e);
        }
    }
}