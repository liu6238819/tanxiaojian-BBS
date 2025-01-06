package com.example.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.bbs.mapper.UserManageMapper;
import com.example.bbs.mapper.UserMapper;
import com.example.bbs.mapper.UserSchoolMapper;
import com.example.bbs.pojo.User;
import com.example.bbs.pojo.vo.EditUserInfoVO;
import com.example.bbs.pojo.vo.SearchUserVO;
import com.example.bbs.service.UserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/3 10:56 上午
 */
@Service
public class UserManageServiceImpl implements UserManageService {
    @Autowired
    private UserManageMapper userManageMapper;


    @Autowired
    UserMapper userMapper;

    @Autowired
    UserSchoolMapper userSchoolMapper;

    @Override
    public IPage<User> getSearchUserList(SearchUserVO searchUserVO, int pageNum, int pageSize) {
        //在这里写方法体
        //这里拼凑搜索条件

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(!Objects.equals(searchUserVO.getPhone(), null)){
            queryWrapper.eq("phone",searchUserVO.getPhone());
        }
        if(!Objects.equals(searchUserVO.getNickName(), null)){
            queryWrapper.eq("nickName",searchUserVO.getNickName());
        }
        if(!Objects.equals(searchUserVO.getSchool(),null)){
            queryWrapper.eq("school",searchUserVO.getSchool());
        }
        if(!Objects.equals(searchUserVO.getUserState(), null)){
            queryWrapper.eq("userState",searchUserVO.getUserState());
        }
        if(!Objects.equals(searchUserVO.getUserType(), null)){
            queryWrapper.eq("userType",searchUserVO.getUserType());
        }
        Page<User> userPage = new Page<>(pageNum , pageSize);
        IPage<User> userIPage = userManageMapper.selectPage(userPage , queryWrapper);
        return userIPage;
    }

    @Override
    public Integer editUser(EditUserInfoVO editUserInfoVO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", editUserInfoVO.getUserId());
        User user = userManageMapper.selectOne(queryWrapper);
        if(user!=null){
            if(!Objects.equals(editUserInfoVO.getUserType(), null)){
                user.setUserType(editUserInfoVO.getUserType());
            }
            if(!Objects.equals(editUserInfoVO.getUserState(), null)) {
                user.setUserState(editUserInfoVO.getUserState());
            }
            return userManageMapper.updateById(user);
        }else{
            return -1;
        }
    }

    @Override
    public Integer findUserByOpenId(String openId,String unionId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openId",openId);
        User user = userManageMapper.selectOne(queryWrapper);
        if(user!=null){
            if (!unionId.equals(user.getUnionId())){
                //数据库记录unionId
                user.setUnionId(unionId);
                userManageMapper.updateById(user);
            }
           return 1;
        }else{
            return 0;
        }
    }


}
