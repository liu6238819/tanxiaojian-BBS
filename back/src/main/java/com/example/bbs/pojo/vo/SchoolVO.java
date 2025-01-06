package com.example.bbs.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.bbs.pojo.School;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SchoolVO {
    private long schoolId;
    private String schoolName;
    private String anotherName;
    private String schoolType;
    private String schoolAddress;
    private int plateNum;
    private int userNum;
    private int contentNum;
    private int state;
    private String baseUrl;
    private String webSocketUrl;
    private String appId;

    public SchoolVO(School school) {
        this.schoolId = school.getSchoolId();
        this.schoolName = school.getSchoolName();
        this.anotherName = school.getAnotherName();
        this.schoolType = school.getSchoolType();
        this.schoolAddress = school.getSchoolAddress();
        this.plateNum = school.getPlateNum();
        this.userNum = school.getUserNum();
        this.contentNum = school.getContentNum();
        this.state = school.getState();
        this.baseUrl = school.getBaseUrl();
        this.webSocketUrl = school.getWebSocketUrl();
        this.appId = school.getAppId();
    }
}
