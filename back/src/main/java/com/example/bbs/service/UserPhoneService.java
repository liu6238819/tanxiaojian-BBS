package com.example.bbs.service;


import com.alibaba.fastjson.JSONObject;
import com.example.bbs.pojo.User;
import com.example.bbs.pojo.UserPhone;
import com.example.bbs.pojo.User_Content;

import java.util.List;

public interface UserPhoneService {
    int createUserPhoneRecord(User user);
}
