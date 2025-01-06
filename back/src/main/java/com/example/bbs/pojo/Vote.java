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
@TableName("bbs_vote")
@ApiModel(value = "投票选项实体对象")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Vote {
    @ApiModelProperty(value = "唯一id",hidden = true)
    @TableId(value = "voteId", type = IdType.UUID)
    private String voteId;
    @ApiModelProperty(value = "所属帖子id")
    @TableField("contentId")
    private String contentId;
    @ApiModelProperty(value = "是否是多选")
    @TableField("isMultiple")
    private int isMultiple;
    @ApiModelProperty(value = "投票人数")
    @TableField("ticketNum")
    private int ticketNum;
    @ApiModelProperty(value = "投票内容")
    @TableField("optionText")
    private String optionText;
    @ApiModelProperty(value = "截止时间")
    @TableField("deadline")
    private Date deadline;
    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;
    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
