package com.example.bbs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.bbs.pojo.User;
import com.example.bbs.pojo.vo.EditUserInfoVO;
import com.example.bbs.pojo.vo.SearchUserVO;

import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/3 10:56 上午
 */
public interface UserManageService {
    IPage<User> getSearchUserList(SearchUserVO searchUserVO, int pageNum, int pageSize);
    //修改用户信息的函数
    Integer editUser(EditUserInfoVO editUserInfoVO);
    Integer findUserByOpenId(String openId,String unionId);
}
