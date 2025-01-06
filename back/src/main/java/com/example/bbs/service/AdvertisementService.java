package com.example.bbs.service;


import com.alibaba.fastjson.JSONObject;
import com.example.bbs.pojo.AdvSignUp;
import com.example.bbs.pojo.Advertisement;

import java.util.List;

public interface AdvertisementService {
    int createOneAdv(Advertisement advertisement);
    List<Advertisement> getAdvList(int schoolId ,String advPosition);
    Advertisement getOneAdvById(String advId, String userId);
    JSONObject createAdvSignUp(AdvSignUp advSignUp);
    JSONObject getAdvSignUp(String activityId, String userId);
    List<Advertisement> getAdvListWithImageBase64(int schoolId ,String advPosition);
    List<Advertisement> getAdvList0507(int schoolId ,int advPosition);
    JSONObject createMultiBusinessAdvSignUp(AdvSignUp advSignUp);

}