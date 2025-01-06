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
@TableName("bbs_remind")
@ApiModel(value = "通知对象")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Remind {
    @ApiModelProperty(value = "唯一id",hidden = true)
    @TableId(value = "id",type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "用户id")
    @TableField("userId")
    private String userId;

    @ApiModelProperty(value = "通知类型，0：赞帖子；1：赞评论；2：关注；3：私信；4：回复帖子；5：回复评论")
    @TableField("type")
    private int type;

    @ApiModelProperty(value = "通知状态： 0：未读 1：已读")
    @TableField("state")
    private int state;

    @ApiModelProperty(value = "行为的发起者")
    @TableField("fromUserId")
    private String fromUserId;

    @ApiModelProperty(value = "互动行为id，type0-3分别对应admire/comment/focus/chat表\t")
    @TableField("relateId")
    private String relateId;

    @ApiModelProperty(value = "通知阅读的时间",hidden = true)
    @TableField(value = "readTime", fill = FieldFill.INSERT)
    private Date readTime;

    @ApiModelProperty(value = "生成通知的时间",hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
