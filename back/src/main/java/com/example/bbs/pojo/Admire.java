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
@TableName("bbs_admire")
@ApiModel(value = "点赞实体对象")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Admire {
    @ApiModelProperty(value = "唯一id", hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "操作者id")
    @TableField("userId")
    private String userId;
    @ApiModelProperty(value = "帖子/评论id")
    @TableField("targetId")
    private String targetId;
    @ApiModelProperty(value = "1：点赞；0：取消赞；2：点踩；3：取消踩")
    @TableField("isLike")
    private int isLike;
    @ApiModelProperty(value = "1：帖子；0：评论；")
    @TableField("isContent")
    private int isContent;
    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;
    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
