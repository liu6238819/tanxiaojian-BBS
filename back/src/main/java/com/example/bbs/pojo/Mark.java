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
@TableName("bbs_mark")
@ApiModel(value = "马住实体对象")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Mark {
    @ApiModelProperty(value = "唯一id", hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "操作者id")
    @TableField("user_id")
    private String userId;
    @ApiModelProperty(value = "帖子id")
    @TableField("content_id")
    private String contentId;
    @ApiModelProperty(value = "1：马住；0：取消马住")
    @TableField("is_mark")
    private int isMark;
    @ApiModelProperty(value = "1：已读；0：未读")
    @TableField("is_read")
    private int isRead;
    @ApiModelProperty(value = "最新评论时间", hidden = true)
    @TableField(value = "last_comment_time", fill = FieldFill.INSERT)
    private Date lastCommnetTime;
    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
