package com.example.studentservice.config;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {


    @Bean
    public Cloudinary getCloudinary() {
        Cloudinary cloudinary = null;
        Map config = new HashMap();
        config.put("cloud_name", "ddlobmuuv");

        config.put("api_key","139856511923716");
        config.put("api_secret","i7Je6fCuZsclzG4kyO8b3s8N5hw");
        config.put("secure",true);
        cloudinary = new Cloudinary(config);
        return cloudinary;
        }
    }

