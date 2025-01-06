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
@TableName("bbs_check_record")
@ApiModel(value = "用户审核记录表")
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class CheckRecord implements Serializable {
    @ApiModelProperty(value = "唯一主键",hidden = true)
    @TableId(value = "id",type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "发帖用户id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty(value = "帖子id")
    @TableField("content_id")
    private String contentId;

    @ApiModelProperty(value = "学校ID")
    @TableField("school_id")
    private int schoolId;

    @ApiModelProperty(value = "是否需要复核")
    @TableField("check_state")
    private int checkState;

    @ApiModelProperty(value = "审核内容")
    @TableField("check_text")
    private String checkText;

    @ApiModelProperty(value = "违规状态")
    @TableField("break_rule_state")
    private int breakRuleState;

    @ApiModelProperty(value = "违规原因")
    @TableField("break_rule_reason")
    private String breakRuleReason;

    @ApiModelProperty(value = "审核时间",hidden = true)
    @TableField(value = "check_time", fill = FieldFill.INSERT)
    private Date checkTime;

    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}

