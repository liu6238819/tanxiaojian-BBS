package com.example.bbs.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("task_order")
@ApiModel(value = "任务订单")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TaskOrder implements Serializable {
    @ApiModelProperty(value = "订单id", hidden = true)
    @TableId(value = "order_id", type = IdType.UUID)
    private String orderId;
    @ApiModelProperty(value = "发布人id")
    @TableField("publisher_id")
    private String publisherId;
    @ApiModelProperty(value = "订单类型")
    @TableField("order_kind")
    private int orderKind;
    @ApiModelProperty(value = "订单状态")
    @TableField("order_state")
    private int orderState;
    @ApiModelProperty(value = "订单内容")
    @TableField("order_content")
    private String orderContent;
    @ApiModelProperty(value = "订单图片")
    @TableField("order_img")
    private String orderImg;
    @ApiModelProperty(value = "订单起点")
    @TableField("order_start_place")
    private String orderStartPlace;
    @ApiModelProperty(value = "订单终点")
    @TableField("order_end_place")
    private String orderEndPlace;
    @ApiModelProperty(value = "专业要求")
    @TableField("applicant_major")
    private String applicantMajor;
    @ApiModelProperty(value = "学历要求")
    @TableField("applicant_qualification")
    private String applicantQualification;
    @ApiModelProperty(value = "备注")
    @TableField("order_remark")
    private String orderRemark;
    @ApiModelProperty(value = "订单金额")
    @TableField("order_cost")
    private BigDecimal orderCost;
    @ApiModelProperty(value = "所属学校")
    @TableField("school_id")
    private int schoolId;
    @ApiModelProperty(value = "申诉理由")
    @TableField("appeal_reason")
    private String appealReason;
    @ApiModelProperty(value = "申诉时间")
    @TableField("appeal_time")
    private Date appealTime;
    @ApiModelProperty(value = "开始时间", hidden = true)
    @TableField(value = "start_time")
    private Date startTime;
    @ApiModelProperty(value = "单方面确认时间", hidden = true)
    @TableField(value = "confirm_time")
    private Date confirmTime;
    @ApiModelProperty(value = "结束时间", hidden = true)
    @TableField(value = "finish_time")
    private Date finishTime;
    @ApiModelProperty(value = "截止时间")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(value = "order_deadline")
    private Date orderDeadline;
    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT )
    private Date createTime;
    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
