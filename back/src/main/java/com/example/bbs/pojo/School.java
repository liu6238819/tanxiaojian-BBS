package com.example.bbs.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.example.bbs.pojo.vo.SchoolVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bbs_school")
@ApiModel(value = "学校实体对象")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class School implements Serializable {
    @ApiModelProperty(value = "学校id", hidden = true)
    @TableId(value = "schoolId", type = IdType.AUTO)
    private long schoolId;
    @ApiModelProperty(value = "学校名称")
    @TableField("schoolName")
    private String schoolName;
    @ApiModelProperty(value = "学校别名")
    @TableField("anotherName")
    private String anotherName;
    @ApiModelProperty(value = "学校类型")
    @TableField("schoolType")
    private String schoolType;
    @ApiModelProperty(value = "学校位置")
    @TableField("schoolAddress")
    private String schoolAddress;
    @ApiModelProperty(value = "学校下的板块数目")
    @TableField("plateNum")
    private int plateNum;
    @ApiModelProperty(value = "学校下的用户数目")
    @TableField("userNum")
    private int userNum;
    @ApiModelProperty(value = "学校的帖子数目")
    @TableField("contentNum")
    private int contentNum;
    @ApiModelProperty(value = "学校状态")
    @TableField("state")
    private int state;
    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;
    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @ApiModelProperty(value = "对应链接前缀")
    @TableField("baseUrl")
    private String baseUrl;
    @ApiModelProperty(value = "对应链接前缀")
    @TableField("webSocketUrl")
    private String webSocketUrl;
    @ApiModelProperty(value = "对应APPID")
    @TableField("appId")
    private String appId;


    public School(SchoolVO schoolVO) {
        this.schoolName = schoolVO.getSchoolName();
        this.anotherName = schoolVO.getAnotherName();
        this.schoolType = schoolVO.getSchoolType();
        this.schoolAddress = schoolVO.getSchoolAddress();
        this.plateNum = schoolVO.getPlateNum();
        this.userNum = schoolVO.getUserNum();
        this.contentNum = schoolVO.getContentNum();
        this.state = schoolVO.getState();
        this.baseUrl = schoolVO.getBaseUrl();
        this.webSocketUrl = schoolVO.getWebSocketUrl();
        this.appId = schoolVO.getAppId();
    }
}
