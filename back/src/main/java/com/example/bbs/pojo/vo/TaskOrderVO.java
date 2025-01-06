package com.example.bbs.pojo.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.bbs.pojo.TaskOrderApplicant;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskOrderVO {
    private String orderId;
    private String publisherId;
    private int orderKind;
    private int orderState;
    private String orderContent;
    private String orderImg;
    private String orderStartPlace;
    private String orderEndPlace;
    private String applicantMajor;
    private String applicantQualification;
    private String orderRemark;
    private BigDecimal orderCost;
    private int schoolId;
    private String appealReason;
    private Date appealTime;
    private Date orderDeadline;
    private Date createTime;
    private Date updateTime;
    //各状态变更时间
    private Date startTime;
    private Date confirmTime;
    private Date finishTime;
    //发布人信息
    private String publisherHeadImgUrl;
    private String publisherNickName;
    private String phone;
    private String openId;
    //接取状态，针对接取者
    private int applicantState;
    //接取人列表
    private List<TaskOrderApplicant> applicants;

}
