package com.example.bbs.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sign_up_info")
@ApiModel(value = "报名信息")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignUpInfo {
    @ApiModelProperty(value = "唯一id",hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "活动id")
    @TableId(value = "activity_id")
    private String activityId;
    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private String userId;
    @ApiModelProperty(value = "批次信息")
    @TableField("batch_number")
    private String batchNumber;
    @ApiModelProperty(value = "报名详情")
    @TableField("info_json")
    private Object infoJson;
    @ApiModelProperty(value = "支付状态")
    @TableField("state")
    private int state;
    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT )
    private Date createTime;
    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
