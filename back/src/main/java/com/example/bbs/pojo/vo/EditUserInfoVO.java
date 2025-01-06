package com.example.bbs.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/3 5:08 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
//用户id，用户状态，用户类型
public class EditUserInfoVO {
    String userId;
    Integer userState;
    Integer userType;
}
