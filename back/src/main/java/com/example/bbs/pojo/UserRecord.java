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
@TableName("bbs_user_record")
@ApiModel(value = "用户业务表")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRecord {
    @ApiModelProperty(value = "唯一id", hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "用户id")
    @TableField("userId")
    private String userId;
    @ApiModelProperty(value = "学校id")
    @TableField("schoolId")
    private int schoolId;
    @ApiModelProperty(value = "当日被举报的帖子数量总和")
    @TableField("inContentNum")
    private int inContentNum;
    @ApiModelProperty(value = "当日被举报的评论数量总和")
    @TableField("inCommentNum")
    private int inCommentNum;
    @ApiModelProperty(value = "当日被折叠的帖子数量总和")
    @TableField("foContentNum")
    private int foContentNum;
    @ApiModelProperty(value = "当日被折叠的评论数量总和")
    @TableField("foCommentNum")
    private int foCommentNum;
    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;
    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
