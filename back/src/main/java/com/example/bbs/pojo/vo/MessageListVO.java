package com.example.bbs.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageListVO {
    private String returnType = "messageList";//区分消息来源
    private String userId; //私信人id
    private String headimgUrl; //头像
    private String nickName; //昵称
    private int unReadNum; //未读私信数量
    private String messageContent; //最新私信内容
    private Date createTime; //最新私信创建时间
    //标识用户认证状态
    private int userState = 0;
    //任务私信专用
    private String orderId; //订单id
    private int isOpen = 1;//通道是否开启
    private int orderKind;//订单类型
}
