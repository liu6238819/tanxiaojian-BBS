package com.example.bbs.pojo.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.example.bbs.pojo.Plate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlateVO {

    private String plateId;

    private int cateId;

    private String userId;

    private int schoolId;

    private String name;

    private String introduction;

    private String portraitUrl;

    private String backImgUrl;

    private int contentNum;

    private int upNum;

    private int downNum;

    private int plateState;

    private Date createTime;

    private Date updateTime;

    private boolean isJoin;

    private int userNum;


    public PlateVO(Plate plate){
        this.plateId = plate.getPlateId();
        this.cateId = plate.getCateId();
        this.userId = plate.getUserId();
        this.schoolId = plate.getSchoolId();
        this.name = plate.getName();
        this.introduction = plate.getIntroduction();
        this.portraitUrl = plate.getPortraitUrl();
        this.backImgUrl = plate.getBackImgUrl();
        this.contentNum = plate.getContentNum();
        this.upNum = plate.getUpNum();
        this.downNum = plate.getDownNum();
        this.plateState = plate.getPlateState();
        this.createTime = plate.getCreateTime();
        this.updateTime = plate.getUpdateTime();
        this.userNum = plate.getUserNum();
    }

    public void setJoin(boolean join) {
        isJoin = join;
    }
}
