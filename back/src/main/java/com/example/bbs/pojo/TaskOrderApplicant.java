package com.example.bbs.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.example.bbs.pojo.vo.SchoolVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("task_order_applicant")
@ApiModel(value = "订单_接取人关系")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TaskOrderApplicant implements Serializable {
    @ApiModelProperty(value = "唯一id", hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "订单id")
    @TableField("order_id")
    private String orderId;
    @ApiModelProperty(value = "接取人id")
    @TableField("applicant_id")
    private String applicantId;
    @ApiModelProperty(value = "私聊id")
    @TableField("chat_id")
    private String chatId;
    @ApiModelProperty(value = "接取状态")
    @TableField("applicant_state")
    private int applicantState;
    @ApiModelProperty(value = "确认时间",hidden = true)
    @TableField(value = "confirm_time")
    private Date confirmTime;
    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT )
    private Date createTime;
    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
