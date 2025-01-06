package com.example.bbs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.BbsConfigMapper;
import com.example.bbs.mapper.GroupMemberMapper;
import com.example.bbs.mapper.PopupNoRemindMapper;
import com.example.bbs.pojo.BbsConfig;
import com.example.bbs.pojo.GroupMember;
import com.example.bbs.pojo.PopupNoRemind;
import com.example.bbs.service.GroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GroupMemberServiceImpl implements GroupMemberService {

    @Autowired
    GroupMemberMapper groupMemberMapper;

    @Autowired
    PopupNoRemindMapper popupNoRemindMapper;

    @Autowired
    BbsConfigMapper bbsConfigMapper;


    @Override
    public Boolean needShowPopup(String groupType, int schoolId, String unionId) {
        //首先判断配置项中是否开启弹窗
        QueryWrapper<BbsConfig> batchConfigQueryWrapper = new QueryWrapper<>();
        batchConfigQueryWrapper
                .eq("school_id", schoolId)
                .eq("config_type", "functionConfig");
        BbsConfig configResult = bbsConfigMapper.selectOne(batchConfigQueryWrapper);
        Object configObject =  JSON.parseObject(configResult.getConfigJson().toString());
        JSONObject configJsonObject = (JSONObject) configObject;
        JSONArray configJsonArray = configJsonObject.getJSONArray("data");
        int noRemindDuration = 0;
        for (Object config:configJsonArray) {
            JSONObject configJO = (JSONObject)config;
            //在弹窗配置项中
            if(configJO.getString("function_name").equals("judgeShowPopup")){
                //获取不再提醒天数
                noRemindDuration =configJO.getInteger("no_notice_days");
                if(configJO.getInteger("all_group_state").equals(1) ){
                    break;
                }
                //配置项弹窗开关未开启
                else{
                    return false;
                }
            }
        }
        //判断是否在成员表中
        List<GroupMember> groupMemberList = new ArrayList<>();
        QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
        groupMemberQueryWrapper.eq("school_id", schoolId)
                .eq("group_type",groupType);
        groupMemberList = groupMemberMapper.selectList(groupMemberQueryWrapper);
        for (GroupMember groupMember:groupMemberList) {
            Object userInfoJson = groupMember.getUserInfo();
            JSONObject userInfoJsonObject = JSONObject.parseObject(userInfoJson.toString());
            String sqlUnionId= (String) userInfoJsonObject.get("unionid");
            if (sqlUnionId==null){
                continue;
            }
            if (sqlUnionId.equals(unionId)){
                return false;
            }
        }
        //再判断是否点击过不再提醒
        QueryWrapper<PopupNoRemind> popupNoRemindQueryWrapper = new QueryWrapper<>();
        popupNoRemindQueryWrapper.eq("school_id", schoolId)
                .eq("union_id",unionId);
        PopupNoRemind popupNoRemind = popupNoRemindMapper.selectOne(popupNoRemindQueryWrapper);
        if (popupNoRemind==null){
        }
        else{
            long nowTime = new Date().getTime();
            long updateTime = popupNoRemind.getUpdateTime().getTime();
            if (nowTime-updateTime<=86400000 * noRemindDuration){
                return false;
            }
            else{
            }
        }
        return true;
    }

    @Override
    public Boolean addNoRemindRecord(String userId, String nickName, int schoolId, String unionId) {
        //先判断用户是否在不再提醒表中有记录
        QueryWrapper<PopupNoRemind> popupNoRemindQueryWrapper = new QueryWrapper<>();
        popupNoRemindQueryWrapper.eq("school_id", schoolId)
                .eq("union_id",unionId);
        PopupNoRemind popupNoRemind = popupNoRemindMapper.selectOne(popupNoRemindQueryWrapper);
        if(popupNoRemind==null){
            //没有记录则新建一个不再提醒记录
            PopupNoRemind noRemindRecord = new PopupNoRemind();
            String newUUID = UUID.randomUUID().toString().replace("-", "");
            Date nowDate = new Date();
            noRemindRecord.setId(newUUID);
            noRemindRecord.setSchoolId(schoolId);
            noRemindRecord.setUserId(userId);
            noRemindRecord.setNickName(nickName);
            noRemindRecord.setUnionId(unionId);
            noRemindRecord.setCreateTime(nowDate);
            noRemindRecord.setUpdateTime(nowDate);
            popupNoRemindMapper.insert(noRemindRecord);
        }
        else{
            //有记录则更新不再提醒表中的updateTime
            popupNoRemind.setUpdateTime(new Date());
            popupNoRemindMapper.updateById(popupNoRemind);
        }
        return null;
    }

}
