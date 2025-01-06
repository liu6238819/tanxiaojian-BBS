package com.example.bbs.service;

import com.example.bbs.pojo.UserRecord;

public interface UserRecordService {
    void updateContentRecordNum(String targetId, int type);
    void updateCommentRecordNum(String targetId,int type);
    UserRecord getUserRecord(String userId,int schoolId);
    UserRecord getUserRecordByDate(String userId,int schoolId,int day);
}
