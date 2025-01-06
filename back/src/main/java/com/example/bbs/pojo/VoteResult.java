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
@TableName("bbs_vote_result")
@ApiModel(value = "投票结果记录")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VoteResult {
    @ApiModelProperty(value = "唯一id",hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "所属帖子id")
    @TableField("contentId")
    private String contentId;
    @ApiModelProperty(value = "选项id")
    @TableField("voteId")
    private String voteId;
    @ApiModelProperty(value = "用户id")
    @TableField("userId")
    private String userId;
    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;

}
