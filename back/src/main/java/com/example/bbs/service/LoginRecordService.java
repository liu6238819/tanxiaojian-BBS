package com.example.bbs.service;


import com.example.bbs.pojo.LoginRecord;

public interface LoginRecordService {
    int createLoginRecord(String userId ,int schoolId, String loginPath);
}
