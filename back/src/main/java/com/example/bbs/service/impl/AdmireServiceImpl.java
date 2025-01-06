package com.example.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.auto.ConAndComTypeAutoEdit;
import com.example.bbs.auto.PermissionCheck;
import com.example.bbs.mapper.*;
import com.example.bbs.pojo.*;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.*;
import com.example.bbs.util.ScoreUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdmireServiceImpl implements AdmireService {
    @Autowired
    AdmireMapper admireMapper;
    @Autowired
    ContentService contentService;
    @Autowired
    CommentService commentService;
    @Autowired
    ConAndComTypeAutoEdit conAndComTypeAutoEdit;
    @Autowired
    ContentMapper contentMapper;
    @Autowired
    RemindMapper remindMapper;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    ScoreUtil scoreUtil;
    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;

    @Override
    public boolean isLikeByUserId(String userId, String targetId, int isContent) {
        QueryWrapper<Admire> admireQueryWrapper = new QueryWrapper<>();
        admireQueryWrapper
                .eq("userId", userId)
                .eq("targetId", targetId)
                .eq("isContent", isContent)
                .eq("isLike", 1);
        Admire admire = admireMapper.selectOne(admireQueryWrapper);
        return admire != null;
    }

    @Override
    public int editLikeNum(String userId, String targetId, int isContent, int isLike, StringBuilder message) {
        int rs = 0;
        if (isContent == 1) {//对帖子的操作
            rs = contentService.upAndDownNumEditByContentId(targetId, isLike);
        }
        if (isContent == 0) {//对评论的操作
            rs = commentService.upNumByCommentId(targetId, isLike);
        }
        if (isLike == 1) {//增加点赞/点踩信息
            rs = addInfo(userId, targetId, isContent, isLike);
            if (rs == 1){//仅新增点赞时发送通知
                addRemind(userId, targetId, isContent);
            }
        }
        if (isLike == 0) {//取消点赞/点踩信息
            rs = delInfo(userId, targetId, isContent, isLike);
        }

        return rs;
    }

    @Override
    public String getPlateId(Admire admire) {
        if (admire.getIsContent() == 1) {
            Content content = contentService.getContent(admire.getTargetId());
            return content.getPlateId();
        } else {
            Comment comment = commentService.getComment(admire.getTargetId());
            return commentService.getPlateId(comment);
        }
    }

    //增加点赞点踩信息
    private int addInfo(String userId, String targetId, int isContent, int isLike) {
        User user = userService.getUserByUserId(userId);
        //初始化
        int result = 0;
        String relatedId = "";
        Admire admire = new Admire();
        admire.setUserId(userId);
        admire.setTargetId(targetId);
        admire.setIsContent(isContent);
        admire.setIsLike(isLike);

        QueryWrapper<Admire> admireQueryWrapper = new QueryWrapper<>();
        admireQueryWrapper
                .eq("userId", userId)
                .eq("targetId", targetId)
                .eq("isContent", isContent);
        Admire record = admireMapper.selectOne(admireQueryWrapper);
        if (record == null) {
            admireMapper.insert(admire);
            relatedId = admire.getId();
            result = 1;
        } else { //已有记录，更改状态
            record.setIsLike(isLike);
            admireMapper.updateById(record);
            relatedId = record.getId();
            result = 2;//重复点赞
        }
        if (result == 1 && !StringUtils.isBlank(user.getHeadimgUrl())) {//点赞成功后，授权用户会根据今日点赞数量获取积分奖励
            if (scoreUtil.getActionNum(userId, 3) <= 3) {
                scoreUtil.changeScore(userId, 3, 5, relatedId);
            } else {
                System.out.println("今日积分获取已达上限！");
            }
        }
        return result;

    }

    //取消点赞信息
    private int delInfo(String userId, String targetId, int isContent, int isLike) {
        QueryWrapper<Admire> admireQueryWrapper = new QueryWrapper<>();
        admireQueryWrapper
                .eq("userId", userId)
                .eq("targetId", targetId)
                .eq("isContent", isContent);
        Admire admire = admireMapper.selectOne(admireQueryWrapper);
        if (null == admire) return -1;
        admire.setIsLike(isLike);
        return admireMapper.updateById(admire);
    }

    private int addRemind(String userId, String targetId, int isContent) {
        Remind remind = new Remind();
        if (isContent == 1) {
            QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
            contentQueryWrapper.eq("contentId", targetId);
            Content content = contentMapper.selectOne(contentQueryWrapper);
            remind.setUserId(content.getUserId());
            remind.setType(0);
            remind.setState(0);
            remind.setFromUserId(userId);
            remind.setRelateId(targetId);
            if (content.getUserId().equals(userId)) return 2;
            int result = remindMapper.insert(remind);
            return result;
        } else {
            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.eq("commentId", targetId);
            Comment comment = commentMapper.selectOne(commentQueryWrapper);
            remind.setUserId(comment.getUserId());
            remind.setType(1);
            remind.setState(0);
            remind.setFromUserId(userId);
            remind.setRelateId(targetId);
            if (comment.getUserId().equals(userId)) return 2;
            int result = remindMapper.insert(remind);
            return result;
        }
    }

}
