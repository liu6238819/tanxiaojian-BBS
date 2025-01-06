package com.example.bbs.service;

import com.example.bbs.pojo.UserSchool;
import com.example.bbs.result.CommonResult;

import java.util.List;

public interface UserSchoolService {
    void updateType(String userId, int schoolId, int type);

    UserSchool getUserSchoolItem(String userId, int schoolId);

    List<UserSchool> getWarnType(String userId);

    CommonResult permisionCheck(String userId, int schoolId, String plateId);

    CommonResult permissionCheckOutBBS(String userId, int schoolId, int schoolFlag);

    int offlineIdentification(String userId, int schoolId, String code);

    int answerIdentification(String userId, int schoolId);

    int haveIdentifiedInOtherSchool(String userId, int schoolId);

    int createUserSchoolRecord(String userId, int schoolId);
}
