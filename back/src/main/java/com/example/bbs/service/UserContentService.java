package com.example.bbs.service;


import com.example.bbs.pojo.User_Content;
import com.example.bbs.pojo.Content;
import com.alibaba.fastjson.JSONObject;
import java.util.List;

public interface UserContentService {
    int createReadRecord(User_Content user_content);
    boolean isFirstRead(String contentId, String userId);
    boolean isFirstReadNew(String contentId, String userId);
}
