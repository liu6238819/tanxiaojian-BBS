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
 * @Date: 2022/2/4 11:11 上午
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bbs_token")
@ApiModel(value = "管理端token表")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Token implements Serializable {
    @ApiModelProperty(value = "唯一id",hidden = true)
    @TableId(value = "id",type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "token值")
    @TableField("accessToken")
    private String accessToken;

    @ApiModelProperty(value = "APPID")
    @TableField("appId")
    private String appId;

    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
