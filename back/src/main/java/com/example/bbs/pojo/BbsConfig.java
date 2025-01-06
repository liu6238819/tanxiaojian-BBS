package com.example.bbs.pojo;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import springfox.documentation.spring.web.json.Json;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bbs_config")
@ApiModel(value = "论坛配置")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BbsConfig implements Serializable {
    @ApiModelProperty(value = "唯一id", hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @ApiModelProperty(value = "学校id")
    @TableField("school_id")
    private int schoolId;
    @ApiModelProperty(value = "配置类型")
    @TableField("config_type")
    private String configType;
    @ApiModelProperty(value = "配置Json")
    @TableField("config_json")
    private Object configJson;
    @ApiModelProperty(value = "群发状态")
    @TableField("state")
    private int state;
    @ApiModelProperty(value = "小程序id")
    @TableField("app_id")
    private String appId;
    @ApiModelProperty(value = "机器人id")
    @TableField("robot_id")
    private String robotId;
    @ApiModelProperty(value = "群发列表")
    @TableField("group_list")
    private String groupList;
    @ApiModelProperty(value = "上次群发时间", hidden = true)
    @TableField(value = "last_send_time")
    private Date lastSendTime;
    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT )
    private Date createTime;
    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
