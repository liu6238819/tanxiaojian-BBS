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
@TableName("bbs_login_statistics")
@ApiModel(value = "用户登录统计表")
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class LoginStatistics implements Serializable {
    @ApiModelProperty(value = "唯一主键",hidden = true)
    @TableId(value = "id",type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "学校id")
    @TableField("school_id")
    private int schoolId;

    @ApiModelProperty(value = "登录人数")
    @TableField("user_count")
    private int userCount;

    @ApiModelProperty(value = "登录次数")
    @TableField("login_count")
    private int loginCount;

    @ApiModelProperty(value = "登录日期")
    @TableField(value = "login_date")
    private Date loginDate;

    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}

