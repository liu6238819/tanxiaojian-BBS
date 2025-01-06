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
@TableName("bbs_chat")
@ApiModel(value = "私信对象")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Chat {
    @ApiModelProperty(value = "唯一id",hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "发送者id")
    @TableField("senderId")
    private String senderId;
    @ApiModelProperty(value = "接受者id")
    @TableField("receiverId")
    private String receiverId;
    @ApiModelProperty(value = "拥有者id")
    @TableField("userId")
    private String userId;
    @ApiModelProperty(value = "对话另一方id")
    @TableField("chaterId")
    private String chaterId;
    @ApiModelProperty(value = "消息类型，1：普通消息；2：系统消息")
    @TableField("messageType")
    private int messageType;
    @ApiModelProperty(value = "消息内容")
    @TableField("messageContent")
    private String messageContent;
    @ApiModelProperty(value = "消息状态，1：未读；2：已读")
    @TableField("state")
    private int state;
    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;
    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
