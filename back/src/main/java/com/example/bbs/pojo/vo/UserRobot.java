package com.example.bbs.pojo.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRobot {
    private String id;
    private String nickName;
    private String headimgUrl;
    private int schoolId;
}
