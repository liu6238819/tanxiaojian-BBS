package com.example.bbs.auto;

import com.example.bbs.pojo.*;
import com.example.bbs.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PermissionCheck {

    @Autowired
    UserService userService;
    @Autowired
    ContentService contentService;
    @Autowired
    CommentService commentService;
    @Autowired
    AdmireService admireService;
    @Autowired
    PlateService plateService;
    @Autowired
    UserSchoolService userSchoolService;

    //对发帖权限进行检查
    public boolean contentPermissionCheck(Content content, StringBuilder message) {
        return check(content.getUserId(), content, 1, message);
    }

    //对评论权限进行检查
    public boolean commentPermissionCheck(Comment comment, StringBuilder message) {
        return check(comment.getUserId(), comment, 2, message);
    }

    //对点赞/点踩权限进行检查
    public boolean admirePermissioncheck(Admire admire, StringBuilder message) {
        return check(admire.getUserId(), admire, 3, message);
    }

    //操作类型1：帖子；2：评论；3：点踩；
    private boolean check(String userId, Object object, int operationType, StringBuilder message) {
        Content content = null;
        Comment comment = null;
        Admire admire = null;
        int schoolId = 0;
        switch (operationType) {
            case 1:
                content = (Content) object;
                schoolId = content.getSchoolId();
                break;
            case 2:
                comment = (Comment) object;
                schoolId = contentService.getContent(comment.getContentId()).getSchoolId();
                break;
            case 3:
                admire = (Admire) object;
                int isContent = admire.getIsContent();
                String targetId = admire.getTargetId();
                if (isContent == 1) {
                    schoolId = contentService.getContent(targetId).getSchoolId();
                }
                if (isContent == 0) {
                    schoolId = contentService.getContent(commentService.getComment(targetId).getContentId()).getSchoolId();
                }
                break;
        }
        UserSchool userSchool = userSchoolService.getUserSchoolItem(userId, schoolId);
        if (userSchool == null) {
            message.append("没有初始化当前用户在当前学校的权限！");
            return false;
        }
        switch (userSchool.getUserType()) {
            case 0://访客
                message.append("没有权限，待审核！");
                return false;
            case 1://微信登录用户
                message.append("没有权限，待审核！");
                return false;
            case 2://审核中用户
                message.append("没有权限，待审核！");
                return false;
            case 3://审核通过用户
                //获取操作的板块id
                String plateId = "";
                if (operationType == 1) {
                    plateId = content.getPlateId();
                }
                if (operationType == 2) {
                    plateId = commentService.getPlateId(comment);
                }
                if (operationType == 3) {
                    plateId = admireService.getPlateId(admire);
                }
                //判断用户是否关注该板块
                if (!plateService.isJoin(plateId, userSchool.getUserId())) {
                    message.append("未关注板块！");
                    return false;
                }
                return true;
            case 4://预警用户
                message.append("预警用户！");
                return true;
            case 5://禁言用户
                message.append("禁言用户，需要申诉");
                return false;
            case 6://管理员
                return true;
            case 7://预警管理员
                message.append("预警管理员，需要申诉");
                return false;
        }
        return true;
    }

}
