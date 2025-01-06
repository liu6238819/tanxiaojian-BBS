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
@TableName("bbs_group_member")
@ApiModel(value = "资讯群群成员列表")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GroupMember implements Serializable {
    @ApiModelProperty(value = "唯一id", hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "学校id")
    @TableField("school_id")
    private int schoolId;
    @ApiModelProperty(value = "用户状态")
    @TableField("member_state")
    private int memberState;
    @ApiModelProperty(value = "群类型")
    @TableField("group_type")
    private String groupType;
    @ApiModelProperty(value = "成员信息Json")
    @TableField("user_info")
    private Object userInfo;
    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT )
    private Date createTime;
    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
