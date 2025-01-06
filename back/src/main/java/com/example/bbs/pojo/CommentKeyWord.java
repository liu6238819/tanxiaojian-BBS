package com.example.bbs.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bbs_comment_key_word")
@ApiModel(value = "评论关键词关系表")
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class CommentKeyWord implements Serializable {
    @ApiModelProperty(value = "唯一主键",hidden = true)
    @TableId(value = "id",type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "用户id")
    @TableField("userId")
    private String userId;

    @ApiModelProperty(value = "帖子id")
    @TableField("contentId")
    private String contentId;

    @ApiModelProperty(value = "评论Id")
    @TableField("commentId")
    private String commentId;

    @ApiModelProperty(value = "帖子的关键词")
    @TableField("keyWord")
    private String keyWord;

    @ApiModelProperty(value = "帖子的总结")
    @TableField("summary")
    private String summary;

    @ApiModelProperty(value = "评论创建时间",hidden = true)
    @TableField(value = "commentCreateTime")
    private Date commentCreateTime;

    @ApiModelProperty(value = "初次阅读时间",hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "最近更新时间",hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}

