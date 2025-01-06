package com.example.bbs.service.impl;

import com.example.bbs.mapper.*;
import com.example.bbs.service.ShortUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    ContentMapper contentMapper;

    @Autowired
    ShortUrlMapper shortUrlMapper;


    private String generateRandomString(String characters, int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }
        return sb.toString();
    }

}
