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
@TableName("bbs_user_school")
@ApiModel(value = "用户学校关系表")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserSchool {
    @ApiModelProperty(value = "唯一id", hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @TableField("userId")
    private String userId;
    @TableField("schoolId")
    private int schoolId;
    @ApiModelProperty(value = "用户类型。0：普通用户；1：预警用户；2：禁言用户；3：管理员；4：预警管理员；")
    @TableField("inviter")
    private String inviter;
    @TableField("stuNum")
    private String stuNum;
    @TableField("idCardUrl")
    private String idCardUrl;
    @TableField("userType")
    private int userType;
    @ApiModelProperty(value = "用户状态。0：访客；1：审核中；2：审核通过；")
    @TableField("userState")
    private int userState;
    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;
    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
