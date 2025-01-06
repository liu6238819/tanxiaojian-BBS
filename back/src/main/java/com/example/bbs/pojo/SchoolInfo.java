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
@TableName("bbs_schoolinfo")
@ApiModel(value = "校园信息记录")
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class SchoolInfo {
    @ApiModelProperty(value = "唯一id",hidden = true)
    @TableId(value = "infoId", type = IdType.UUID)
    private String infoId;
    @ApiModelProperty(value = "主贴id")
    @TableField("contentId")
    private String contentId;
    @ApiModelProperty(value = "信息内容")
    @TableField("intro")
    private String intro;
    @ApiModelProperty(value = "图片链接")
    @TableField("imgUrls")
    private String imgUrls;
    @ApiModelProperty(value = "联系人")
    @TableField("linkman")
    private String linkman;
    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;
    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
