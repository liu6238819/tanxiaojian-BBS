package com.example.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.UserRecordMapper;
import com.example.bbs.pojo.Comment;
import com.example.bbs.pojo.Content;
import com.example.bbs.pojo.UserRecord;
import com.example.bbs.service.CommentService;
import com.example.bbs.service.ContentService;
import com.example.bbs.service.UserRecordService;
import com.example.bbs.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRecordServiceImpl implements UserRecordService {
    @Autowired
    ContentService contentService;
    @Autowired
    UserRecordMapper userRecordMapper;
    @Autowired
    CommentService commentService;

    @Override
    public void updateContentRecordNum(String targetId, int type) {
        Content content = contentService.getContent(targetId);
        UserRecord userRecord = getUserRecord(content.getUserId(), content.getSchoolId());
        if (userRecord == null) {
            userRecord = new UserRecord();
        }
        userRecord.setUserId(content.getUserId());
        userRecord.setSchoolId(content.getSchoolId());
        if (type == 1) {//被折叠帖子
            userRecord.setFoContentNum(userRecord.getFoContentNum() + 1);
        } else if (type == 5) {//被举报帖子
            userRecord.setInContentNum(userRecord.getInContentNum() + 1);
        }
        if (getUserRecord(content.getUserId(), content.getSchoolId()) == null) {
            userRecordMapper.insert(userRecord);
        } else {
            userRecordMapper.updateById(userRecord);
        }
    }

    @Override
    public void updateCommentRecordNum(String targetId, int type) {
        Comment comment = commentService.getComment(targetId);
        Content content = contentService.getContent(comment.getContentId());
        UserRecord userRecord = getUserRecord(comment.getUserId(), content.getSchoolId());
        if (userRecord == null) {
            userRecord = new UserRecord();
        }
        userRecord.setUserId(comment.getUserId());
        userRecord.setSchoolId(content.getSchoolId());
        if (type == 1) {//被折叠评论
            userRecord.setFoCommentNum(userRecord.getFoCommentNum() + 1);
        } else if (type == 5) {//被举报评论
            userRecord.setInCommentNum(userRecord.getInCommentNum() + 1);
        }
        if (getUserRecord(comment.getUserId(), content.getSchoolId()) == null) {
            userRecordMapper.insert(userRecord);
        } else {
            userRecordMapper.updateById(userRecord);
        }
    }

    @Override
    public UserRecord getUserRecord(String userId, int schoolId) {
        QueryWrapper<UserRecord> userRecordQueryWrapper = new QueryWrapper<>();
        userRecordQueryWrapper.eq("userId", userId).eq("schoolId", schoolId).gt("createTime", DateUtil.getSystemDate(0));
        UserRecord userRecord = userRecordMapper.selectOne(userRecordQueryWrapper);
        return userRecord;
    }

    @Override
    public UserRecord getUserRecordByDate(String userId, int schoolId, int day) {
        String date = DateUtil.getSystemDate(day);
        return userRecordMapper.getUserRecordByDate(userId, schoolId, date);
    }
}
