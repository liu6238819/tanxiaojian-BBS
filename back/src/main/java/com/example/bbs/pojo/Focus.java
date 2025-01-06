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
 * @Date: 2022/2/21 8:14 下午
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bbs_focus")
@ApiModel(value = "关注表")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Focus implements Serializable {

    @ApiModelProperty(value = "唯一id", hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "用户ID")
    @TableField("userId")
    private String userId;

    @ApiModelProperty(value = "关注用户ID")
    @TableField("targetId")
    private String targetId;

    @ApiModelProperty(value = "关注状态")
    @TableField("state")
    private Integer state;

    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
