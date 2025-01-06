package com.example.bbs.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bbs_comment")
@ApiModel(value = "评论实体对象")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Comment {
    @ApiModelProperty(value = "唯一id",hidden = true)
    @TableId(value = "commentId", type = IdType.UUID)
    private String commentId;
    @ApiModelProperty(value = "发表评论的用户id")
    @TableField("userId")
    private String userId;
    @ApiModelProperty(value = "帖子的id")
    @TableField("contentId")
    private String contentId;
    @ApiModelProperty(value = "子评论所在主评论的id")
    @TableField("replyCommentId")
    private String replyCommentId;
    @ApiModelProperty(value = "是否是子评论对子评论的评论1：是；0：否")
    @TableField("isReply")
    private int isReply;
    @ApiModelProperty(value = "子评论回复的子评论对象名称")
    @TableField("replyNickName")
    private String replyNickName;
    @ApiModelProperty(value = "子评论回复的对象的id")
    @TableField("replyUserId")
    private String replyUserId;
    @ApiModelProperty(value = "1：主评论；0：评论的评论")
    @TableField("isMain")
    private int isMain;
    @ApiModelProperty(value = "评论的文字部分")
    @TableField("commentText")
    private String commentText;
    @ApiModelProperty(value = "评论的图片部分")
    @TableField("commentUrl")
    private String commentUrl;
    @ApiModelProperty(value = "评论的子评论数")
    @TableField("childrenNum")
    private int childrenNum;
    @ApiModelProperty(value = "评论的点赞数")
    @TableField("upNum")
    private int upNum;
    @ApiModelProperty(value = "评论的踩数")
    @TableField("downNum")
    private int downNum;
    @ApiModelProperty(value = "举报数量")
    @TableField("informNum")
    private int informNum;
    @ApiModelProperty(value = "评论的状态")
    @TableField("commentState")
    private int commentState;
    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;
    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
