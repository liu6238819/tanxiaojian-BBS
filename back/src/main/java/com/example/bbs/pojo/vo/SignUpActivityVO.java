package com.example.bbs.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignUpActivityVO {
    private String id;
    private String image;
    private String content;
    private int kind;
    private int state;
    private int schoolId;
    private String ownerId;//拥有者Id
    private int alumniOnly;
    private String link;//外部链接
    private BigDecimal deposit;
    private int quota;
    private Date deadline;
    private Date createTime;
    private Date updateTime;
    private Object infoJson;
    //报名人数
    private int signUpNum;
}
