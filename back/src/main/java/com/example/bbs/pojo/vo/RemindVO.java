package com.example.bbs.pojo.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.bbs.pojo.Remind;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RemindVO {
    private String id;
    private int type;
    private int state;
    private String fromUserId;
    private String relateId;
    private String contentId;
    private String userNickName;
    private String userAvatar;
    private Date createTime;
    //标识用户认证状态
    private int userState = 0;
    //标识消息相关帖子帖子所在学校
    private int schoolId;

    public RemindVO(Remind remind){
        this.id=remind.getId();
        this.type=remind.getType();
        this.state=remind.getState();
        this.fromUserId=remind.getFromUserId();
        this.relateId=remind.getRelateId();
        this.createTime=remind.getCreateTime();
    }
}
