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
@TableName("sign_up_activity")
@ApiModel(value = "报名活动")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignUpActivity {
    @ApiModelProperty(value = "唯一id",hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "活动图片")
    @TableId(value = "image")
    private String image;
    @ApiModelProperty(value = "内容简介")
    @TableField("content")
    private String content;
    @ApiModelProperty(value = "活动类型")
    @TableField("kind")
    private int kind;
    @ApiModelProperty(value = "活动状态")
    @TableField("state")
    private int state;
    @ApiModelProperty(value = "所在学校")
    @TableField("school_id")
    private int schoolId;
    @ApiModelProperty(value = "拥有者id")
    @TableField("owner_id")
    private String ownerId;
    @ApiModelProperty(value = "是否仅限校友参与")
    @TableField("alumni_only")
    private int alumniOnly;
    @ApiModelProperty(value = "外部链接")
    @TableField("link")
    private String link;
    @ApiModelProperty(value = "报名定金")
    @TableField("deposit")
    private BigDecimal deposit;
    @ApiModelProperty(value = "报名名额")
    @TableField("quota")
    private int quota;
    @ApiModelProperty(value = "截止时间")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(value = "deadline")
    private Date deadline;
    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT )
    private Date createTime;
    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
