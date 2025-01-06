package com.example.bbs.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.ActivityOwnerMapper;
import com.example.bbs.mapper.SignUpActivityMapper;
import com.example.bbs.pojo.ActivityOwner;
import com.example.bbs.pojo.SignUpActivity;
import com.example.bbs.pojo.SignUpInfo;
import com.example.bbs.pojo.vo.SignUpActivityVO;
import com.example.bbs.service.ActivityOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityOwnerServiceImpl implements ActivityOwnerService {
    @Autowired
    ActivityOwnerMapper activityOwnerMapper;

    @Override
    public int createActivityOwner(ActivityOwner activityOwner) {
        int result = 0;
        //创建活动拥有者
        ActivityOwner record = new ActivityOwner();
        record.setName(activityOwner.getName());
        record.setSchoolId(activityOwner.getSchoolId());
        record.setIntro(activityOwner.getIntro());
        record.setImage(activityOwner.getImage());
        record.setKind(activityOwner.getKind());
        record.setState(activityOwner.getState());
        record.setAlumniOnly(activityOwner.getAlumniOnly());
        record.setLink(activityOwner.getLink());
        result = activityOwnerMapper.insert(record);
        if (result <= 0) return result;
        return result;
    }

    @Override
    public List<ActivityOwner> getActivityOwnerList(String ownerId, int kind, int state, int schoolId, int pageNum, int pageSize) {
        List<ActivityOwner> activityOwnerList = null;
        activityOwnerList = activityOwnerMapper.selectActivityOwnerList(ownerId,kind, state, schoolId, (pageNum - 1) * pageSize, pageSize);
        return activityOwnerList;
    }

}
