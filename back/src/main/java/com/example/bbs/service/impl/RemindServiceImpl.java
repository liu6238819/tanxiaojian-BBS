package com.example.bbs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.*;
import com.example.bbs.pojo.*;
import com.example.bbs.pojo.vo.RemindVO;
import com.example.bbs.service.RemindService;
import com.example.bbs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@EnableCaching
public class RemindServiceImpl implements RemindService {

    @Autowired
    RemindMapper remindMapper;
    @Autowired
    UserMapper userMapper;

    @Autowired
    ContentMapper contentMapper;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    MarkMapper markMapper;
    @Autowired
    AdmireMapper admireMapper;



    @Autowired
    UserService userService;

    @Override
    public List<RemindVO> getRemindsPage(String userId,String queryType, Integer schoolId, int pageNum, int pageSize) {
        List<RemindVO> vo = remindMapper.getRemindsPage(userId, queryType,(pageNum - 1) * pageSize, pageSize);
        List<RemindVO> copy = new ArrayList<RemindVO>();

        for (RemindVO item : vo
        ) {
            User user = userService.getUserByUserId(item.getFromUserId());
            if (null==user)continue;
            if (null == user.getHeadimgUrl()) {
                item.setUserNickName("一位游客");
            }else {
                item.setUserNickName(user.getNickName());
                item.setUserAvatar(user.getHeadimgUrl());
            }
            int currentUserState = userService.getUserStateBySchool(item.getFromUserId(),schoolId).getInteger("userState");
            item.setUserState(currentUserState);
            //对帖子的相关操作，relateId就是帖子Id
            if (item.getType() == 0 || item.getType() == 4) {
                item.setContentId(item.getRelateId());
                item = dealRemind(item);
                copy.add(item);
                continue;
            }
            //对评论的相关操作，帖子Id为跨表查询值
            if (item.getType() == 1 || item.getType() == 5) {
                item.setContentId(item.getContentId());
                item = dealRemind(item);
                copy.add(item);
                continue;
            } else {
                //其他情况，无需返回帖子Id
                item.setContentId(null);
                copy.add(item);
                continue;
            }


        }
        return copy;
    }

    @Override
    public int selectNums(String userId,String queryType) {

        return remindMapper.selectNums(userId,queryType);
    }

    @Override
    public boolean readRemind(String userId, String remindId) {
        Remind remind = remindMapper.selectById(remindId);
        if (userId.equals(remind.getUserId())) {
            remind.setState(1);
            remind.setReadTime(new Date());
            try {
                remindMapper.updateById(remind);
                return true;
            } catch (Exception e) {
                return false;
            }

        } else {
            return false;
        }
    }

    @Override
    public boolean clearRemind(String userId, Integer type) {
        List<Remind> reminds = new ArrayList<Remind>();
        QueryWrapper<Remind> remindQueryWrapper = new QueryWrapper<>();
        remindQueryWrapper.eq("userId", userId);
        remindQueryWrapper.eq("state", 0);
        if (type == 1) {//点赞相关消息
            remindQueryWrapper.and(wrapper -> wrapper.eq("type", 0).or().eq("type", 1));
        }
        if (type == 2) {//关注相关消息
            remindQueryWrapper.and(wrapper -> wrapper.eq("type", 2));
        }
        if (type == 3) {//评论相关消息
            remindQueryWrapper.and(wrapper -> wrapper.eq("type", 4).or().eq("type", 5));
        }
        reminds = remindMapper.selectList(remindQueryWrapper);

        if (null != reminds) {
            for (int i = 0; i < reminds.size(); i++) {
                Remind remind = reminds.get(i);
                remind.setState(1);
                remind.setReadTime(new Date());
                try {
                    remindMapper.updateById(remind);
                } catch (Exception e) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private RemindVO dealRemind(RemindVO remindVO){
        QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("contentId",remindVO.getContentId());
        Content content = contentMapper.selectOne(contentQueryWrapper);
        if (null==content){
            return remindVO;
        }else {
            if(content.getContentType()==3 || content.getIsSpecial()==1 ||content.getIsSpecial()==4){ //匿名贴处理
                remindVO.setUserAvatar(null);
                remindVO.setUserNickName("一位隐士");
            }
        }
        return remindVO;
    }

    @Override
    public JSONObject editRemindByContentId(String userId, String contentId){
        int reduceAdmireCount = 0;
        int reduceCommentCount = 0;
        List<Remind> reminds = new ArrayList<Remind>();
        List<Integer> typeList =  Arrays.asList(0,4);
        QueryWrapper<Remind> remindQueryWrapper = new QueryWrapper<>();
        //寻找与这个帖子有关的remind记录（除对评论的评论外）
        remindQueryWrapper
                .eq("userId", userId)
                .eq("state", 0)
                .eq("relateId",contentId)
                .in("type", typeList);
        reminds = remindMapper.selectList(remindQueryWrapper);
        if (null==reminds) {
            return null;
        }
        else{
            for (Remind remind : reminds){
                remind.setState(1);
                remind.setReadTime(new Date());
                remindMapper.updateById(remind);
                if (remind.getType()==4){
                    reduceCommentCount = reduceCommentCount +1;
                }
                else if(remind.getType()==0){
                    reduceAdmireCount =reduceAdmireCount+1;
                }

            }
        }
        //寻找与这个帖子有关的子评论的Remind记录
        List<Remind> reminds2 = new ArrayList<Remind>();
        QueryWrapper<Remind> remindQueryWrapper2 = new QueryWrapper<>();
        List<Integer> typeList2 =  Arrays.asList(1,5);
        remindQueryWrapper2
                .eq("userId", userId)
                .eq("state", 0)
                .in("type", typeList2);
        reminds2 = remindMapper.selectList(remindQueryWrapper2);
        if (null==reminds2) {
            return null;
        }
        else{
            for (Remind remind2 : reminds2){
                Comment commentFromContent = new Comment();
                commentFromContent = commentMapper.selectById(remind2.getRelateId());
                if (null==commentFromContent) {
                    return null;
                }
                else{
                    if (Objects.equals(commentFromContent.getContentId(), contentId)){
                        remind2.setState(1);
                        remind2.setReadTime(new Date());
                        remindMapper.updateById(remind2);
                        if (remind2.getType()==5){
                            reduceCommentCount = reduceCommentCount +1;
                        }
                        else if(remind2.getType()==1){
                            reduceAdmireCount =reduceAdmireCount+1;
                        }
                    }
                }
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("reduceCommentCount",reduceCommentCount );
        jsonObject.put("reduceAdmireCount",reduceAdmireCount );
        return jsonObject;
    }

    @Override
    public List<RemindVO> getRemindsPageWithoutschoolId0102(String userId, String queryType, int pageNum, int pageSize) {
        //        List<RemindVO> vo = remindMapper.getRemindsPageDistinguishSchoolId(userId, queryType,(pageNum - 1) * pageSize, pageSize);
        List<RemindVO> vo = remindMapper.getRemindsPageDistinguishSchoolId0102(userId, queryType,(pageNum - 1) * pageSize, pageSize);
        for (RemindVO remindVO:vo) {
            //对评论操作，0为点赞，4为评论
            if (remindVO.getType()==1 || remindVO.getType()==5){
                Comment comment= commentMapper.selectById(remindVO.getRelateId());
                remindVO.setContentId(comment.getContentId());
            }
        }

        List<RemindVO> copy = new ArrayList<RemindVO>();
        for (RemindVO item : vo
        ) {
            User user = userService.getUserByUserId(item.getFromUserId());
            if (null==user)continue;
            if (null == user.getHeadimgUrl()) {
                item.setUserNickName("一位游客");
            }else {
                item.setUserNickName(user.getNickName());
                item.setUserAvatar(user.getHeadimgUrl());
            }

            //对帖子的相关操作，relateId就是帖子Id
            if (item.getType() == 0 || item.getType() == 4) {
                item.setContentId(item.getRelateId());
                item = dealRemind(item);
                copy.add(item);
                continue;
            }
            //对评论的相关操作，帖子Id为跨表查询值
            if (item.getType() == 1 || item.getType() == 5) {
                item.setContentId(item.getContentId());
                item = dealRemind(item);
                copy.add(item);
                continue;
            } else {
                //其他情况，无需返回帖子Id
                item.setContentId(null);
                copy.add(item);
                continue;
            }


        }
        return copy;
    }

}
