package com.example.bbs.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.bbs.pojo.User;
import com.example.bbs.pojo.UserSchool;
import com.example.bbs.pojo.vo.HomeContentVO;
import com.example.bbs.pojo.vo.SearchUserVO;
import com.example.bbs.result.CommonResult;

import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/1/11 9:28 下午
 */

public interface UserService {

    boolean userRegister(User user, String ipAddr);

    User userLogin(String phone, String password);

    User userLoginWX(String openId);

    int editUserInfos(User user);

    int editUserInfoFront(User user);

    User getUserByConOrComId(String targetId,int isContent);

    void updateUserType(String userId, int state);

    User getUserByUserId(String userId);

    Integer FollowRecordUpdate(String userId, String targetId, Integer isFollow);

    JSONObject getUserInfoById(String userId,String searchId, Integer schoolId);

    JSONObject getFollowedUsers(String userId, Integer type, int pageNum, int pageSize);

    JSONObject getLikedContents(String userId, int pageNum, int pageSize,Integer schoolId);

    JSONObject getUserStateBySchool(String userId, Integer schoolId);

    int manageUserInfo(UserSchool userSchool);

    int userLogOut(String userId, String headImgUrl ,String nickName ,int schoolId);




}
