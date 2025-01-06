package com.example.bbs.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sign_up_activity_owner")
@ApiModel(value = "活动拥有者")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ActivityOwner {
    @ApiModelProperty(value = "唯一id",hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "名称")
    @TableId(value = "name")
    private String name;
    @ApiModelProperty(value = "所在学校")
    @TableField("school_id")
    private int schoolId;
    @ApiModelProperty(value = "内容简介")
    @TableField("intro")
    private String intro;
    @ApiModelProperty(value = "图片")
    @TableId(value = "image")
    private String image;
    @ApiModelProperty(value = "类型")
    @TableField("kind")
    private int kind;
    @ApiModelProperty(value = "状态")
    @TableField("state")
    private int state;
    @ApiModelProperty(value = "是否仅校友参与")
    @TableField("alumni_only")
    private int alumniOnly;
    @ApiModelProperty(value = "外部链接")
    @TableId(value = "link")
    private String link;
    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT )
    private Date createTime;
    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
