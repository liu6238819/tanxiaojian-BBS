package com.example.bbs.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChildrenCommentVO implements Serializable {
    //评论的相关信息
    private String commentId;
    private String contentId;
    private String replyCommentId;
    private String isReply;
    private String replyNickName;
    private String replyUserId;
    private String commentText;
    private String commentUrl;
    private int upNum;
    private int downNum;
    private int commentState;
    private Date createTime;
    //评论者相关信息
    private String userId;
    private String openId;
    private String headimgUrl;
    private String nickName;
    private int viewUserisLike;
    //标识用户认证状态
    private int userState = 0;
    //网文社身份标识
    private int userIdentify;
}
