package com.example.bbs.auto;

import com.example.bbs.pojo.User;
import com.example.bbs.pojo.UserRecord;
import com.example.bbs.pojo.UserSchool;
import com.example.bbs.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserTypeAutoEdit {

    @Autowired
    UserService userService;
    @Autowired
    ContentService contentService;
    @Autowired
    CommentService commentService;
    @Autowired
    UserRecordService userRecordService;
    @Autowired
    UserSchoolService userSchoolService;

    //帖子或者评论状态变更为折叠或者举报，触发用户状态变更。
    public void changeUserType(String targetId, int isContent) {
        //获取用户信息
        User user = userService.getUserByConOrComId(targetId, isContent);
        //获取帖子或评论的学校信息
        int schoolId = 0;
        if (isContent == 1) {
            schoolId = contentService.getContent(targetId).getSchoolId();
        }
        if (isContent == 0) {
            schoolId = contentService.getContent(commentService.getComment(targetId).getContentId()).getSchoolId();
        }
        //获取单日内信息
        UserRecord oneRecord = userRecordService.getUserRecordByDate(user.getUserId(), schoolId, 0);
        //获取三日内信息
        UserRecord threeRecord = userRecordService.getUserRecordByDate(user.getUserId(), schoolId, -2);
        check(user.getUserId(), schoolId, oneRecord, threeRecord);
    }

    //规则检查
    private void check(String userId, int schoolId, UserRecord oneRecord, UserRecord threeRecord) {
        User user = userService.getUserByUserId(userId);
        //单日内举报状态帖子数量
        int oneInContentNum = oneRecord.getInContentNum();
        //单日内举报状态评论数量
        int oneInCommentNum = oneRecord.getInCommentNum();
        //单日内折叠状态帖子数量
        int oneFoContentNum = oneRecord.getFoContentNum();
        //单日内折叠状态评论数量
        int oneFoCommentNum = oneRecord.getFoCommentNum();
        //三日内举报状态帖子数量
        int threeInContentNum = threeRecord.getInContentNum();
        //三日内举报状态评论数量
        int threeInCommentNum = threeRecord.getInCommentNum();
        //三日内折叠状态帖子数量
        int threeFoContentNum = threeRecord.getFoContentNum();
        //三日内折叠状态评论数量
        int threeFoCommentNum = threeRecord.getFoCommentNum();
        //4预警用户，单日内举报状态帖子/评论数>1，折叠状态的帖子/评论数>3，或三日内折叠状态的帖子/评论数>5
        if ((oneInContentNum + oneInCommentNum > 1 && oneFoContentNum + oneFoCommentNum > 3) ||
                (threeFoContentNum + threeFoCommentNum > 5)) {
            if (user.getUserType() == 6) {
                //变更管理员为预警管理员
                changeType(userId, schoolId, 7);
            } else {
                //变更普通用户为预警用户
                changeType(userId, schoolId, 4);
            }
        }
        //5禁言用户，单日内举报状态帖子/评论数>2，折叠状态的帖子/评论数>5，或三日内举报状态帖子/评论数>2，折叠状态的帖子/评论数>7
        if ((oneInContentNum + oneInCommentNum > 2 && oneFoContentNum + oneFoCommentNum > 5) ||
                (threeInContentNum + threeInContentNum > 2 && threeFoContentNum + threeFoCommentNum > 7)) {
            if (user.getUserType() == 6) {
                //变更管理员为预警管理员
                changeType(userId, schoolId, 7);
            } else {
                //变更普通用户为禁言用户
                changeType(userId, schoolId, 5);
            }
        }
    }

    //根据用户id和学校id更新用户类型
    private void changeType(String userId, int schoolId, int type) {
        userSchoolService.updateType(userId, schoolId, type);
    }

    //预警用户变为正常
    public void WarnToNormalUser(User user) {
        List<UserSchool> warnList = userSchoolService.getWarnType(user.getUserId());
        if (warnList == null) return;
        for (UserSchool userSchool : warnList) {
            UserRecord userRecordByDate = userRecordService.getUserRecordByDate(user.getUserId(), userSchool.getSchoolId(), -2);
            if (userRecordByDate == null) {
                changeType(user.getUserId(), userSchool.getSchoolId(), 3);
                log.info("预警用户：" + user.getUserId() + "，变为正常用户");
            }
        }
    }

}
