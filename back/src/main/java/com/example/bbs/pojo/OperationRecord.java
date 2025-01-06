package com.example.bbs.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bbs_operation_record")
@ApiModel(value = "操作记录")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OperationRecord {
    @ApiModelProperty(value = "唯一id",hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "学校id")
    @TableField("school_id")
    private int schoolId;
    @ApiModelProperty(value = "操作记录状态")
    @TableField("record_state")
    private int recordState;
    @ApiModelProperty(value = "操作人")
    @TableField("operator")
    private String operator;
    @ApiModelProperty(value = "操作对象类型")
    @TableField("target_kind")
    private int targetKind;
    @ApiModelProperty(value = "操作对象id")
    @TableField("target_id")
    private String targetId;
    @ApiModelProperty(value = "变更的内容")
    @TableField("change_info")
    private String changeInfo;
    @ApiModelProperty(value = "备注")
    @TableField("note")
    private String note;
    @ApiModelProperty(value = "操作时间")
    @TableField(value = "operate_time")
    private Date operateTime;
    @ApiModelProperty(value = "被禁言时间")
    @TableField(value = "banned_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date bannedTime;
    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
