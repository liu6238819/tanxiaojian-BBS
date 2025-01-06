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
@TableName("bbs_sign")
@ApiModel(value = "签到记录")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Sign {
    @ApiModelProperty(value = "唯一id",hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "用户id")
    @TableField("userId")
    private String userId;
    @ApiModelProperty(value = "签到信息")
    @TableField("mark")
    private String mark;
    @ApiModelProperty(value = "签到数")
    @TableField("count")
    private int count;
    @ApiModelProperty(value = "7日签到数")
    @TableField("countInSeven")
    private int countInSeven;
    @ApiModelProperty(value = "当日首次登录")
    @TableField("firstSign")
    private int firstSign;
    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;
    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
