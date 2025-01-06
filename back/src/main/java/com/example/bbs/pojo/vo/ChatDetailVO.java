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
public class ChatDetailVO {
    private String userId;
    private String senderId;
    private String messageContent;
    private Date createTime;
}
