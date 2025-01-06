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
@TableName("task_chat")
@ApiModel(value = "任务私信")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TaskChat {
    @ApiModelProperty(value = "唯一id",hidden = true)
    @TableId(value = "chat_id", type = IdType.UUID)
    private String chatId;
    @ApiModelProperty(value = "订单id",hidden = true)
    @TableId(value = "order_id")
    private String orderId;
    @ApiModelProperty(value = "发送者id")
    @TableField("sender_id")
    private String senderId;
    @ApiModelProperty(value = "接受者id")
    @TableField("receiver_id")
    private String receiverId;
    @ApiModelProperty(value = "拥有者id")
    @TableField("user_id")
    private String userId;
    @ApiModelProperty(value = "对话另一方id")
    @TableField("chater_id")
    private String chaterId;
    @ApiModelProperty(value = "消息类型")
    @TableField("message_type")
    private int messageType;
    @ApiModelProperty(value = "消息内容")
    @TableField("message_Content")
    private String messageContent;
    @ApiModelProperty(value = "消息状态")
    @TableField("message_state")
    private int messageState;
    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT )
    private Date createTime;
    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
