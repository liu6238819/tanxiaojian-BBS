package com.example.bbs.pojo.vo;

import com.example.bbs.pojo.SchoolInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HomeCommentVO implements Serializable {
    //评论的相关信息
    private String commentId;
    private String contentId;
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
    private List<ChildrenCommentVO> children = new ArrayList<>();
    //标识用户认证状态
    private int userState = 0;
    //网文社身份标识
    private int userIdentify;
    //热度标识
    private int hotNum;
    //子评论总数
    private int childrenNum;
    //是否存在更多子评论
    private int haveMoreChildren;
    //子评论显示状态
    private int childrenShowState;
    //标志是否获取过子评论存入缓存中
    private int setChildrenToRedis;
}
