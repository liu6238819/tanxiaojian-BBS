package com.example.bbs.auto;

import com.example.bbs.pojo.Comment;
import com.example.bbs.pojo.Content;
import com.example.bbs.service.CommentService;
import com.example.bbs.service.ContentService;
import com.example.bbs.service.UserRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConAndComTypeAutoEdit {

    @Autowired
    ContentService contentService;
    @Autowired
    CommentService commentService;
    @Autowired
    UserRecordService userRecordService;

    //帖子/评论状态动态修改
    public String changeConAndComType(String targetId, int isContent) {
        int upNum = 0;//点赞数量
        int downNum = 0;//点踩数量
        int informNum = 0;//举报数量
        Content content = null;
        Comment comment = null;
        if (isContent == 1) {//帖子
            content = contentService.getContent(targetId);
        } else if (isContent == 0) {//评论
            comment = commentService.getComment(targetId);
        }
        upNum = content != null ? content.getUpNum() : comment.getUpNum();
        downNum = content != null ? content.getDownNum() : comment.getDownNum();
        informNum = content != null ? content.getInformNum() : comment.getInformNum();
        //检查规则
        return check(targetId, isContent, upNum, downNum, informNum);
    }

    private String check(String targetId, int isContent, int upNum, int downNum, int informNum) {
        int sum = upNum + downNum;
        String message = "";
        boolean flag = false;
        if (sum <= 15 && downNum > 5 && downNum <= 8) {
            //总交互数（点赞+点踩)<=15，点踩人数大于5人，或举报人数大于3人，状态自动变更为折叠，前端收起。
            message = "需折叠";
            changeType(targetId, isContent, 1);
            flag = true;
        } else if (sum <= 15 && (downNum > 8 || informNum > 3)) {
            //总交互数（点赞+点踩)<=15，点踩人数大于8人，或举报人数大于5人，状态自动变更为举报，前端提示举报。
            changeType(targetId, isContent, 5);
            message = "被举报";
            flag = true;
        } else if (sum > 15 && downNum / (sum * 1.0) > 0.33 && downNum / (sum * 1.0) <= 0.5) {
            //总交互数（点赞+点踩)>15，点踩人数大于33%，或举报人数大于25%，状态自动变更为折叠，前端收起。
            changeType(targetId, isContent, 1);
            message = "需折叠";
            flag = true;
        } else if (sum > 15 && (downNum / (sum * 1.0) > 0.5 || informNum > 5)) {
            //总交互数（点赞+点踩)>15，点踩人数大于50%，或举报人数大于25%，状态自动变更为举报，前端提示举报。
            changeType(targetId, isContent, 5);
            message = "被举报";
            flag = true;
        }
        if (!flag) {
            //经过检查没有问题，将帖子/评论状态重置为正常
            changeType(targetId, isContent, 0);
            return "";
        }
        if (isContent == 1) {
            return "帖子" + message;
        } else {
            return "评论" + message;
        }
    }

    //将状态变为折叠或者举报
    private void changeType(String targetId, int isContent, int type) {
        if (isContent == 1) {
            //变更帖子状态
            contentService.updateContentType(targetId, type);
        } else if (isContent == 0) {
            //变更评论状态
            commentService.updateCommentType(targetId, type);
        }
    }

}
