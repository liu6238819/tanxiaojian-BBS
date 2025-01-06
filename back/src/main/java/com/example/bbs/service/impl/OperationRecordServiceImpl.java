package com.example.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.OperationRecordMapper;
import com.example.bbs.mapper.UserMapper;
import com.example.bbs.mapper.UserSchoolMapper;
import com.example.bbs.pojo.OperationRecord;
import com.example.bbs.pojo.User;
import com.example.bbs.pojo.UserSchool;
import com.example.bbs.service.InformService;
import com.example.bbs.service.OperationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OperationRecordServiceImpl implements OperationRecordService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserSchoolMapper userSchoolMapper;

    @Autowired
    OperationRecordMapper operationRecordMapper;


    @Override
    public int addOperationRecord(OperationRecord operationRecord) {
        operationRecord.setOperateTime(new Date());
        return  operationRecordMapper.insert(operationRecord);
    }

    @Override
    public long isReachBannedTime(String userId, int schoolId) {
        //获取用户的userType,如果被禁言，获取用户的禁言时间
        QueryWrapper<UserSchool> userSchoolQueryWrapper = new QueryWrapper<>();
        userSchoolQueryWrapper.eq("userId", userId)
                .eq("schoolId", schoolId)
                .orderByAsc("createTime");
        List<UserSchool>  userSchool = userSchoolMapper.selectList(userSchoolQueryWrapper);
        if(userSchool.get(0).getUserType()==2){
            //用户处于禁言状态,判断是否到达解禁时间
            QueryWrapper<OperationRecord> operationRecordQueryWrapper = new QueryWrapper<>();
            operationRecordQueryWrapper.eq("target_id",userId)
                    .and(wrapper -> wrapper.isNotNull("banned_time")
                            .or()
                            .like("note", "永久禁言"))
                    .orderByDesc("operate_time")
                    .last("LIMIT 5");
            List<OperationRecord> operationRecords = operationRecordMapper.selectList(operationRecordQueryWrapper);
            //无记录或时间戳为空，返回-1
            if(operationRecords.size()==0 || operationRecords.get(0).getBannedTime()==null){
                return -1;
            }
            //已经到期,修改用户userType
            if (operationRecords.get(0).getBannedTime().getTime() <= new Date().getTime()){
                userSchool.get(0).setUserType(0);
                userSchoolMapper.updateById(userSchool.get(0));
                return 1;
            }
            //返回禁言时间的时间戳
            else{
                return operationRecords.get(0).getBannedTime().getTime();
            }
        }
        //非禁言状态
        else{
            return 1;
        }
    }

    @Override
    public List<OperationRecord> selectBannedList(int schoolId, String targetId) {
        List<OperationRecord> operationRecordList =new ArrayList<>();
        QueryWrapper<OperationRecord> operationRecordQueryWrapper = new QueryWrapper<>();
        operationRecordQueryWrapper
                .eq("target_id", targetId)
                .eq("school_id", schoolId)
                .eq("target_kind", 1)
                .orderByDesc("operate_time")
                .last("limit 5");
        operationRecordQueryWrapper.and(wrapper ->
                wrapper.isNotNull("banned_time")
                        .or()
                        .like("note", "永久禁言")
        );
        operationRecordList = operationRecordMapper.selectList(operationRecordQueryWrapper);
        return operationRecordList;

    }
}
