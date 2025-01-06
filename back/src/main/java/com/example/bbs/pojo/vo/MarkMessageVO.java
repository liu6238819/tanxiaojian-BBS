package com.example.bbs.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MarkMessageVO {

    private String contentId; //帖子Id
    private int isRead;//是否已读
    private int isMark = 1; //马住
    private String contentText; //帖子内容
    private String contentImage; //帖子图片
    private int commentCount; //新增评论数量
    private Date lastCommentTime; //最新评论时间
    private Date createTime; //创建时间

}
