package com.example.bbs.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/1/11 8:58 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bbs_user")
@ApiModel(value = "用户对象")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User implements Serializable {
    @ApiModelProperty(value = "唯一id",hidden = true)
    @TableId(value = "userId",type = IdType.UUID)
    private String userId;
    @ApiModelProperty(value = "手机号")
    @TableField("phone")
    private String phone;
    @ApiModelProperty(value = "密码")
    @TableField("password")
    private String password;
    @ApiModelProperty(value = "邀请人")
    @TableField("inviter")
    private String inviter;
    @ApiModelProperty(value = "学生号")
    @TableField("stuNum")
    private String stuNum;
    @ApiModelProperty(value = "性别")
    @TableField("sex")
    private int sex;
    @ApiModelProperty(value = "城市")
    @TableField("city")
    private String city;
    @ApiModelProperty(value = "邮箱")
    @TableField("email")
    private String email;
    @ApiModelProperty(value = "身份认证照片，以逗号分割的数组")
    @TableField("idCardUrl")
    private String idCardUrl;
    @ApiModelProperty(value = "昵称")
    @TableField("nickName")
    private String nickName;
    @ApiModelProperty(value = "头像地址")
    @TableField("headimgUrl")
    private String headimgUrl;
    @ApiModelProperty(value = "目前以发表板块数量")
    @TableField("plateNum")
    private int plateNum;
    @ApiModelProperty(value = "会员积分")
    @TableField("scores")
    private int scores;
    @ApiModelProperty(value = "openId")
    @TableField("openId")
    private String openId;
    @ApiModelProperty(value = "unionId")
    @TableField("unionId")
    private String unionId;
    @ApiModelProperty(value = "用户类型。0：访客；1：微信登录用户；2：审核中用户；3：审核通过用户；")
    @TableField("userType")
    private int userType;
    @ApiModelProperty(value = "用户状态。0：正常；1：禁言；2：举报；")
    @TableField("userState")
    private int userState;
    @ApiModelProperty(value = "上次登录的ip")
    @TableField("lastIp")
    private String lastIp;
    @ApiModelProperty(value = "上次登录时间")
    @TableField(value = "lastLoginTime")
    private Date lastLoginTime;
    @ApiModelProperty(value = "简介")
    @TableField("introduction")
    private String introduction;
    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;
    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
