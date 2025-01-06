package com.example.bbs.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bbs_adv")
@ApiModel(value = "马住实体对象")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Advertisement implements Serializable {
    @ApiModelProperty(value = "唯一id", hidden = true)
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "广告类型")
    @TableField("kind")
    private int kind;

    @ApiModelProperty(value = "广告状态")
    @TableField("adv_state")
    private int advState;

    @ApiModelProperty(value = "广告显示位置")
    @TableField("show_position")
    private String showPosition;

    @ApiModelProperty(value = "广告图片")
    @TableField("image_urls")
    private String imageUrls;

    @ApiModelProperty(value = "广告标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "学校ID")
    @TableField("school_id")
    private int schoolId;

    @ApiModelProperty(value = "序号")
    @TableField("adv_order")
    private int advOrder;

    @ApiModelProperty(value = "是否展示")
    @TableField("is_show_post_detail")
    private int isShowPostDetail;

    @ApiModelProperty(value = "是否首页展示")
    @TableField("is_show_index")
    private int isShowIndex;

    @ApiModelProperty(value = "真实阅读人数")
    @TableField("real_read_num")
    private int realReadNum;

    @ApiModelProperty(value = "阅读量")
    @TableField("read_num")
    private int readNum;

    @ApiModelProperty(value = "首页图片")
    @TableField("image_index")
    private String imageIndex;

    @ApiModelProperty(value = "横向小图")
    @TableField("image_small")
    private String imageSmall;

    @ApiModelProperty(value = "广告详情页图片")
    @TableField("image_detail")
    private String imageDetail;

    @ApiModelProperty(value = "表单属性")
    @TableField("form_json")
    private Object formJson;

    @ApiModelProperty(value = "多业务广告信息")
    @TableField("adv_info_json")
    private Object advInfoJson;

    @ApiModelProperty(value = "广告小程序ID")
    @TableField("mini_app_id")
    private String miniAppId;

    @ApiModelProperty(value = "广告小程序页面路径")
    @TableField("mini_app_page")
    private String miniAppPage;

    @ApiModelProperty(value = "关联报名活动ID")
    @TableField("activity_id")
    private String activityId;

    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间",hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
