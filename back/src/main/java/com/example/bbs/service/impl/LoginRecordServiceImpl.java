package com.example.bbs.service.impl;

import com.example.bbs.mapper.LoginRecordMapper;
import com.example.bbs.pojo.LoginRecord;
import com.example.bbs.service.LoginRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class LoginRecordServiceImpl implements LoginRecordService {



    @Autowired
    LoginRecordMapper loginRecordMapper;

    @Override
    public int createLoginRecord( String userId ,int schoolId, String loginPath) {
        LoginRecord loginRecord = new LoginRecord();
        String newUUID = UUID.randomUUID().toString().replace("-", "");
        Date nowDate = new Date();
        loginRecord.setId(newUUID);
        loginRecord.setUserId(userId);
        loginRecord.setSchoolId(schoolId);
        loginRecord.setLoginPath(loginPath);
        loginRecord.setLoginTime(nowDate);
        loginRecord.setCreateTime(nowDate);
        loginRecord.setUpdateTime(nowDate);
        int result = loginRecordMapper.insert(loginRecord);
        return result;
    }
}
