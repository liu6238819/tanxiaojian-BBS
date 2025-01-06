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
@TableName("bbs_search_record")
@ApiModel(value = "用户搜索记录表")
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class SearchRecord implements Serializable {
    @ApiModelProperty(value = "唯一主键",hidden = true)
    @TableId(value = "id",type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "搜索用户id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty(value = "搜索内容")
    @TableField("text")
    private String text;

    @ApiModelProperty(value = "学校id")
    @TableField("school_id")
    private int schoolId;

    @ApiModelProperty(value = "搜索帖子类型")
    @TableField("content_type")
    private int contentType;

    @ApiModelProperty(value = "搜索时间",hidden = true)
    @TableField(value = "search_time", fill = FieldFill.INSERT)
    private Date searchTime;

    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}

