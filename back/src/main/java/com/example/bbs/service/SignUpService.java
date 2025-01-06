package com.example.bbs.service;

import com.alibaba.fastjson.JSONObject;
import com.example.bbs.mapper.SignUpActivityMapper;
import com.example.bbs.pojo.SignUpActivity;
import com.example.bbs.pojo.SignUpInfo;
import com.example.bbs.pojo.TaskOrder;
import com.example.bbs.pojo.vo.SignUpActivityVO;
import com.example.bbs.pojo.vo.TaskOrderVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface SignUpService {

    int createSignUpActivity(SignUpActivityVO activityVO);
    JSONObject getSignUpInfo(String activityId, String userId, String batchNumber);
    List<SignUpInfo> getSignUpList(String activityId, String batchNumber);
    List<SignUpActivityVO> getActivityList(String ownerId,int kind, int state, int schoolId,int alumniOnly, int pageNum, int pageSize);
    JSONObject addSignUpInfo(SignUpInfo signUpInfo);
    void payedSignUp(SignUpInfo signUpInfo);

}
