package com.example.bbs.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bbs_cate")
@ApiModel(value = "板块类目类别")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Cate {
    @ApiModelProperty(value = "板块的类目id",hidden = true)
    @TableId("cateId")
    private int cateId;

    @ApiModelProperty(value = "板块的类目名字")
    @TableField("cateName")
    private String cateName;

    @ApiModelProperty(value = "")
    @TableField("isTop")
    private String isTop;

    @ApiModelProperty(value = "")
    @TableField("coverImage")
    private String coverImage;

    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;
    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
