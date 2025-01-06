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
@TableName("bbs_score_record")
@ApiModel(value = "积分记录")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ScoreRecord {
    @ApiModelProperty(value = "唯一id",hidden = true)
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private String userId;
    @ApiModelProperty(value = "行为类型")
    @TableField("action_type")
    private int actionType;
    @ApiModelProperty(value = "关联id")
    @TableField("related_id")
    private String relatedId;
    @ApiModelProperty(value = "改变数量")
    @TableField("change_num")
    private int changeNum;
    @ApiModelProperty(value = "新积分")
    @TableField("new_score")
    private int newScore;
    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT )
    private Date createTime;
    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}

