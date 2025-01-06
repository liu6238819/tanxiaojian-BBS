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
@TableName("bbs_inform")
@ApiModel(value = "举报记录对象")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Inform {
    @ApiModelProperty(value = "唯一id",hidden = true)
    @TableId(value = "informId", type = IdType.UUID)
    private String informId;
    @ApiModelProperty(value = "举报人id")
    @TableField("userId")
    private String userId;
    @ApiModelProperty(value = "学校id")
    @TableField("schoolId")
    private int schoolId;
    @ApiModelProperty(value = "被举报用户id")
    @TableField("informedUserId")
    private String informedUserId;
    @ApiModelProperty(value = "被举报内容帖子id")
    @TableField("contentId")
    private String contentId;
    @ApiModelProperty(value = "被举报内容id")
    @TableField("targetId")
    private String targetId;
    @ApiModelProperty(value = "被举报内容")
    @TableField("targetText")
    private String targetText;
    @ApiModelProperty(value = "举报信息")
    @TableField("informInfo")
    private String informInfo;
    @ApiModelProperty(value = "举报图片")
    @TableField("informUrl")
    private String informUrl;
    @ApiModelProperty(value = "0：评论；1：帖子；")
    @TableField("isContent")
    private int isContent;
    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;
    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
