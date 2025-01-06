package com.example.bbs.service;

import com.alibaba.fastjson.JSONObject;
import com.example.bbs.pojo.ActivityOwner;
import com.example.bbs.pojo.SignUpInfo;
import com.example.bbs.pojo.vo.SignUpActivityVO;

import java.util.List;

public interface ActivityOwnerService {
    int createActivityOwner(ActivityOwner activityOwner);
    List<ActivityOwner> getActivityOwnerList(String ownerId,int kind, int state, int schoolId,int pageNum, int pageSize);

}
