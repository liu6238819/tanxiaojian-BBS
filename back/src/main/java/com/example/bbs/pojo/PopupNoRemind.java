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
@TableName("popup_no_remind")
@ApiModel(value = "不再提醒记录")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PopupNoRemind implements Serializable {
    @ApiModelProperty(value = "唯一id", hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "学校id")
    @TableField("school_id")
    private int schoolId;
    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private String userId;
    @ApiModelProperty(value = "用户昵称")
    @TableField("nick_name")
    private String nickName;
    @ApiModelProperty(value = "用户unionId")
    @TableField("union_id")
    private String unionId;
    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT )
    private Date createTime;
    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
