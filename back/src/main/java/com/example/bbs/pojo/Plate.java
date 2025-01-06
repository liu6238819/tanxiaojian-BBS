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
@TableName("bbs_plate")
@ApiModel(value = "板块实体对象")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Plate implements Serializable {
    @ApiModelProperty(value = "唯一主键",hidden = true)
    @TableId(value = "plateId",type = IdType.UUID)
    private String plateId;

    @ApiModelProperty(value = "板块的类目")
    @TableField("cateId")
    private int cateId;

    @ApiModelProperty(value = "板块创建者id")
    @TableField("userId")
    private String userId;

    @ApiModelProperty(value = "板块所属学校id")
    @TableField("schoolId")
    private int schoolId;

    @ApiModelProperty(value = "板块名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "板块简介")
    @TableField("introduction")
    private String introduction;

    @ApiModelProperty(value = "图标URL")
    @TableField("portraitUrl")
    private String portraitUrl;

    @ApiModelProperty(value = "背景图片URL")
    @TableField("backImgUrl")
    private String backImgUrl;

    @ApiModelProperty(value = "当前板块拥有帖子数量")
    @TableField("contentNum")
    private int contentNum;

    @ApiModelProperty(value = "当前板块关注数量")
    @TableField("userNum")
    private int userNum;

    @ApiModelProperty(value = "点赞数量")
    @TableField("upNum")
    private int upNum;

    @ApiModelProperty(value = "“踩”数量")
    @TableField("downNum")
    private int downNum;

    @ApiModelProperty(value = "板块状态。0：正常；1：需要审核；2：已下架；")
    @TableField("plateState")
    private int plateState;

    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
