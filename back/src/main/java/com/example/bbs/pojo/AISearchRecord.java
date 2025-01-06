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
@TableName("bbs_AI_search_record")
@ApiModel(value = "用户阅读帖子关系表")
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class AISearchRecord implements Serializable {
    @ApiModelProperty(value = "唯一主键",hidden = true)
    @TableId(value = "id",type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty(value = "帖子所属学校id")
    @TableField("school_id")
    private int schoolId;

    @ApiModelProperty(value = "搜索方式")
    @TableField("search_way")
    private int searchWay;

    @ApiModelProperty(value = "搜索文本")
    @TableField("search_text")
    private String searchText;

    @ApiModelProperty(value = "搜索提取的关键词")
    @TableField("key_word")
    private String keyWord;

    @ApiModelProperty(value = "匹配结果的json")
    @TableField("res_json")
    private Object resJson;

    @ApiModelProperty(value = "发布状态的json")
    @TableField("publish_json")
    private Object publishJson;

    @ApiModelProperty(value = "跳转的Json")
    @TableField("navigate_json")
    private Object navigateJson;

    @ApiModelProperty(value = "初次搜索时间",hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "最近更新时间",hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}

