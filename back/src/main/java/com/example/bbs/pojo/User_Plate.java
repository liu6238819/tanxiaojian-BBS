package com.example.bbs.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bbs_user_plate")
@ApiModel(value = "板块关注关系表")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User_Plate {
    @ApiModelProperty(value = "唯一主键",hidden = true)
    @TableId(value = "id",type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "用户id")
    @TableField("userId")
    private String userId;

    @ApiModelProperty(value = "板块id")
    @TableField("plateId")
    private String plateId;

    @ApiModelProperty(value = "关注时间",hidden = true)
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "上次更新时间",hidden = true)
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    public User_Plate(String userId, String plateId) {
        this.userId = userId;
        this.plateId = plateId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPlateId() {
        return plateId;
    }

    public String getId() {
        return id;
    }
}
