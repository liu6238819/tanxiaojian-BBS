package com.example.bbs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.ContentMapper;
import com.example.bbs.mapper.FocusMapper;
import com.example.bbs.mapper.FocusSearchRecordMapper;
import com.example.bbs.mapper.RemindMapper;
import com.example.bbs.pojo.*;
import com.example.bbs.service.FocusService;
import com.example.bbs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/21 8:24 下午
 */
@Service
public class FocusServiceImpl implements FocusService {

    @Autowired
    FocusMapper followMapper;

    @Autowired
    RemindMapper remindMapper;


    @Autowired
    UserService userService;

    @Autowired
    ContentMapper contentMapper;

    @Autowired
    FocusSearchRecordMapper focusSearchRecordMapper;

    @Override
    public void refreshFollowRecord(String userId, String targetId, Integer state) {
                QueryWrapper<Focus> followQueryWrapper = new QueryWrapper<>();
                followQueryWrapper.eq("userId",userId);
                followQueryWrapper.eq("targetId",targetId);
                Focus focus = followMapper.selectOne(followQueryWrapper);
                if(focus !=null){
                    //已经存在的话，直接修改状态
                    focus.setState(state);
                    followMapper.updateById(focus);
                }else{
                    Focus newFocus = new Focus();
                    newFocus.setTargetId(targetId);
                    newFocus.setUserId(userId);
                    newFocus.setState(state);
                    followMapper.insert(newFocus);
                }
    }

    @Override
    public Integer addRemind(String userId, String targetId,Integer state) {
        if(state.equals(1)){
            Remind remind = new Remind();
            remind.setFromUserId(userId);
            remind.setType(2);
            remind.setUserId(targetId);
            remind.setState(0);
            QueryWrapper<Remind> remindQueryWrapper = new QueryWrapper<>();
            remindQueryWrapper.eq("userId",targetId);
            remindQueryWrapper.eq("fromUserId",userId);
            remindQueryWrapper.eq("type",2);
            Remind preRemind = remindMapper.selectOne(remindQueryWrapper);
            if(preRemind==null){
                remindMapper.insert(remind);
            }else{
                remindMapper.delete(remindQueryWrapper);
                remindMapper.insert(remind);
            }
            return 1;
        }else{
            QueryWrapper<Remind> remindQueryWrapper = new QueryWrapper<>();
            remindQueryWrapper.eq("userId",targetId);
            remindQueryWrapper.eq("fromUserId",userId);
            remindQueryWrapper.eq("type",2);
            Remind remind = remindMapper.selectOne(remindQueryWrapper);
            remind.setState(1);
            remindMapper.updateById(remind);
            return 0;
        }
    }

    @Override
    public JSONObject searchFocusContentRecord0103(String userId, int schoolId) {
        JSONObject resJSONObject =new JSONObject();
        //获取该用户的关注列表
        QueryWrapper<Focus> followQueryWrapper = new QueryWrapper<>();
        followQueryWrapper.eq("userId",userId);
        followQueryWrapper.eq("state",1);
        List<Focus> focusList = followMapper.selectList(followQueryWrapper);
        if (focusList==null){
            return null;
        }
        //无关注用户
        if(focusList.size()==0){
            resJSONObject.put("haveFocus",0);
            return resJSONObject;
        }
        //有关注用户
        else{
            resJSONObject.put("haveFocus",1);
            //查询该用户是否有查询记录
            QueryWrapper<FocusSearchRecord> focusSearchRecordQueryWrapper = new QueryWrapper<>();
            focusSearchRecordQueryWrapper.eq("user_id",userId);
            focusSearchRecordQueryWrapper.eq("school_id",schoolId);
            focusSearchRecordQueryWrapper.orderByDesc("create_time");
            List<FocusSearchRecord> focusSearchRecordList = focusSearchRecordMapper.selectList(focusSearchRecordQueryWrapper);
            Date lastSearchTime =new Date();
            FocusSearchRecord focusSearchRecord = new FocusSearchRecord();
            //没有查询记录,使用用户上次登录时间
            if (focusSearchRecordList==null || focusSearchRecordList.size()==0){
                User user = userService.getUserByUserId(userId);
                lastSearchTime = user.getLastLoginTime();
                //新建一个查询记录
                String newUUID = UUID.randomUUID().toString().replace("-", "");
                Date nowDate = new Date();
                focusSearchRecord.setId(newUUID);
                focusSearchRecord.setUserId(userId);
                focusSearchRecord.setSchoolId(schoolId);
                focusSearchRecord.setSearchTime(lastSearchTime);
                focusSearchRecord.setCreateTime(nowDate);
                focusSearchRecord.setUpdateTime(nowDate);
                focusSearchRecordMapper.insert(focusSearchRecord);
            }
            //有查询记录
            else{
                focusSearchRecord =focusSearchRecordList.get(0);
                lastSearchTime = focusSearchRecordList.get(0).getSearchTime();
            }
            //查询在用户从上次查询到现在的关注发帖情况
            List<String> targetIdList = focusList.stream()
                    .map(Focus::getTargetId) // 提取 targetId
                    .collect(Collectors.toList()); // 转为 List 集合
            Map<String, Object> result = contentMapper.selectFocusContentStatistics(targetIdList, schoolId, lastSearchTime);
            Long postCount = (Long) result.get("postCount");
            Date latestPostTime = (Date) result.get("latestPostTime");
            if (latestPostTime!=null){
                focusSearchRecord.setLastContentTime(latestPostTime);
                focusSearchRecordMapper.updateById(focusSearchRecord);
            }
            else{
                latestPostTime = focusSearchRecord.getLastContentTime();
            }
            resJSONObject.put("contentNum",postCount);
            resJSONObject.put("latestContentTime",latestPostTime);
        }
        return resJSONObject;
    }

    @Override
    public int updateFocusSearchTime(String userId, int schoolId) {
        QueryWrapper<FocusSearchRecord> focusSearchRecordQueryWrapper = new QueryWrapper<>();
        focusSearchRecordQueryWrapper.eq("user_id",userId);
        focusSearchRecordQueryWrapper.eq("school_id",schoolId);
        focusSearchRecordQueryWrapper.orderByDesc("create_time");
        List<FocusSearchRecord> focusSearchRecordList = focusSearchRecordMapper.selectList(focusSearchRecordQueryWrapper);
        Date nowDate = new Date();
        FocusSearchRecord focusSearchRecord = new FocusSearchRecord();
        //没有查询记录,使用用户上次登录时间
        if (focusSearchRecordList==null || focusSearchRecordList.size()==0){
            //新建一个查询记录
            String newUUID = UUID.randomUUID().toString().replace("-", "");
            focusSearchRecord.setId(newUUID);
            focusSearchRecord.setUserId(userId);
            focusSearchRecord.setSchoolId(schoolId);
            focusSearchRecord.setSearchTime(nowDate);
            focusSearchRecord.setCreateTime(nowDate);
            focusSearchRecord.setUpdateTime(nowDate);
            focusSearchRecordMapper.insert(focusSearchRecord);
        }
        //有查询记录
        else{
            focusSearchRecord =focusSearchRecordList.get(0);
            focusSearchRecord.setSearchTime(nowDate);
            focusSearchRecordMapper.updateById(focusSearchRecord);
        }
        return 1;
    }

}
