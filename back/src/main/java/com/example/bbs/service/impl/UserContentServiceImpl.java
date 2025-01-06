package com.example.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.ContentMapper;
import com.example.bbs.mapper.User_ContentMapper;
import com.example.bbs.pojo.Content;
import com.example.bbs.pojo.User_Content;
import com.example.bbs.service.UserContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserContentServiceImpl implements UserContentService {

    @Autowired
    User_ContentMapper user_contentMapper;
    @Autowired
    ContentMapper contentMapper;


    @Override
    public boolean isFirstRead(String contentId, String userId) {
        QueryWrapper<User_Content> userContentQueryWrapper = new QueryWrapper<>();
        QueryWrapper<User_Content> contentId1 = userContentQueryWrapper.eq("contentId", contentId);
        List<User_Content> Read_Users = user_contentMapper.selectList(contentId1);
        //判断当前用户是否已经阅读过该贴
        for (int i = 0; i < Read_Users.size(); i++) {
            if (Read_Users.get(i).getUserId().equals(userId)) {
                //该用户，用户阅读关系表中的阅读数+1
                Read_Users.get(i).setReadNum(Read_Users.get(i).getReadNum() + 1);
                user_contentMapper.updateById(Read_Users.get(i));
                return false;
            }
        }
        //如果没有该用户的记录，创建并修改阅读数
        User_Content user_content = new User_Content();
        user_content.setUserId(userId);
        user_content.setContentId(contentId);
        user_content.setUserId(user_content.getUserId());
        user_content.setContentId(user_content.getContentId());
        user_content.setReadNum(1);
        user_contentMapper.insert(user_content);
        //真实阅读数量+1
        QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("contentId", contentId);
        Content content = contentMapper.selectOne(contentQueryWrapper);
        content.setRealReadNum(content.getRealReadNum() + 1);
        contentMapper.updateById(content);
        return true;

    }

    //用户第一次阅读（优化版）
    @Override
    public boolean isFirstReadNew(String contentId, String userId) {
        QueryWrapper<User_Content> userContentQueryWrapper = new QueryWrapper<>();
        userContentQueryWrapper = userContentQueryWrapper
                .eq("contentId", contentId)
                .eq("userId", userId);
        List<User_Content> userContentRecordList = user_contentMapper.selectList(userContentQueryWrapper);
        //查询出错时，直接返回false
        if (userContentRecordList==null){
            return false;
        }
        //获取阅读的帖子，为修改阅读数做准备
        QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("contentId", contentId);
        Content content = contentMapper.selectOne(contentQueryWrapper);
        //首次阅读
        if (userContentRecordList.size()==0){
            //如果数据库没有该用户的记录
            User_Content user_content = new User_Content();
            String newUUID = UUID.randomUUID().toString().replace("-", "");
            Date nowDate = new Date();
            user_content.setId(newUUID);
            user_content.setUserId(userId);
            user_content.setContentId(contentId);
            user_content.setReadNum(1);
            user_content.setCreateTime(nowDate);
            user_content.setUpdateTime(nowDate);
            user_contentMapper.insert(user_content);
//            真实阅读数量+1，阅读次数加1
            content.setRealReadNum(content.getRealReadNum() + 1);
            content.setReadNum(content.getReadNum() + 1);
            contentMapper.updateById(content);
            return true;
        }
        //当前用户是否已经阅读过该贴
        else{
            User_Content userContentRecord = userContentRecordList.get(0);
            //该用户，用户阅读关系表中的阅读数+1
            userContentRecord.setReadNum(userContentRecord.getReadNum()+1);
            user_contentMapper.updateById(userContentRecord);
            //帖子阅读数+1
            content.setReadNum(content.getReadNum() + 1);
            contentMapper.updateById(content);
            return false;
        }
    }

    @Override
    public int createReadRecord(User_Content user_content) {
        user_content.setUserId(user_content.getUserId());
        user_content.setContentId(user_content.getContentId());
        user_content.setReadNum(1);
        user_contentMapper.insert(user_content);
        return 1;
    }
}
