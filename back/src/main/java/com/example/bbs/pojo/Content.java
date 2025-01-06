package com.example.bbs.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bbs_content")
@ApiModel(value = "帖子实体对象")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Content implements Serializable {
    @ApiModelProperty(value = "唯一id", hidden = true)
    @TableId(value = "contentId", type = IdType.UUID)
    private String contentId;
    @ApiModelProperty(value = "发帖用户id")
    @TableField("userId")
    private String userId;
    @ApiModelProperty(value = "帖子所属板块id")
    @TableField("plateId")
    private String plateId;
    @ApiModelProperty(value = "帖子所属学校id")
    @TableField("schoolId")
    private int schoolId;
    @ApiModelProperty(value = "帖子的标题")
    @TableField("title")
    private String title;
    @ApiModelProperty(value = "帖子的文字部分")
    @TableField("contentText")
    private String contentText;
    @ApiModelProperty(value = "帖子的图片部分")
    @TableField("contentUrl")
    private String contentUrl;
    @ApiModelProperty(value = "发帖位置")
    @TableField("place")
    private String place;
    @ApiModelProperty(value = "点赞数量")
    @TableField("upNum")
    private int upNum;
    @ApiModelProperty(value = "踩数量")
    @TableField("downNum")
    private int downNum;
    @ApiModelProperty(value = "评论数")
    @TableField("commentNum")
    private int commentNum;
    @ApiModelProperty(value = "转发数")
    @TableField("forwardNum")
    private int forwardNum;
    @ApiModelProperty(value = "真实阅读人数")
    @TableField("realReadNum")
    private int realReadNum;
    @ApiModelProperty(value = "阅读量")
    @TableField("readNum")
    private int readNum;
    @ApiModelProperty(value = "举报数量")
    @TableField("informNum")
    private int informNum;
    @ApiModelProperty(value = "特殊类型标识")
    @TableField("isSpecial")
    private int isSpecial;
    @ApiModelProperty(value = "帖子可见范围：板块内/校内/校内外")
    @TableField("visibleRange")
    private int visibleRange;
    @ApiModelProperty(value = "帖子状态。0：正常；1：折叠展示；2：需要审核；3：已下架；")
    @TableField("contentState")
    private int contentState;
    @ApiModelProperty(value = "帖子类型。0：普通贴；1：投票贴；2：视频贴；")
    @TableField("contentType")
    private int contentType;
    @ApiModelProperty(value = "不可评论状态。0：可评论；1：不可评论")
    @TableField("noComment")
    private int noComment;
    @ApiModelProperty(value = "仅校友查看状态。0：非仅校友；1：仅校友")
    @TableField("alumniOnly")
    private int alumniOnly;
    @ApiModelProperty(value = "对应到vote表中的项")
    @TableField("voteId")
    private String voteId;
    @TableField(exist = false)
    private List<Vote> votes;
    @TableField(exist = false)
    private List<SchoolInfo> schoolInfos;
    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT )
    private Date createTime;
    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
