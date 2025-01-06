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
@TableName("bbs_user_content")
@ApiModel(value = "用户阅读帖子关系表")
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class User_Content implements Serializable {
    @ApiModelProperty(value = "唯一主键",hidden = true)
    @TableId(value = "id",type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "用户id")
    @TableField("userId")
    private String userId;

    @ApiModelProperty(value = "帖子id")
    @TableField("contentId")
    private String contentId;

    @ApiModelProperty(value = "阅读次数")
    @TableField("readNum")
    private int readNum;

    @ApiModelProperty(value = "初次阅读时间",hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "最近更新时间",hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    public String getUserId() {
        return userId;
    }

    public String getContentId() {
        return contentId;
    }

    public String getId() {
        return id;
    }

    public int getReadNum() {return readNum;}
}

