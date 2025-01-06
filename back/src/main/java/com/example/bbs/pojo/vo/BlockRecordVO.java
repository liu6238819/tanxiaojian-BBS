package com.example.bbs.pojo.vo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class BlockRecordVO implements Serializable {

    private String id;

    private String userId;

    private String targetId;

    private String targetName;

    private int targetState;

    private Date createTime;

    private Date updateTime;

}

