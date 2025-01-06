package com.example.bbs.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/3 10:47 上午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class SearchUserVO {
    String phone;
    String nickName;
    String school;
    Integer userType;
    Integer userState;
    Date updateTime;
    Date createTime;
    String introduction;
    String headimgUrl;
}
